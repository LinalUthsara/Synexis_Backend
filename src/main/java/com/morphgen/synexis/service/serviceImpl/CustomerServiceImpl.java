package com.morphgen.synexis.service.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.morphgen.synexis.dto.CustomerDto;
import com.morphgen.synexis.dto.CustomerSideDropViewDto;
import com.morphgen.synexis.dto.CustomerTableViewDto;
import com.morphgen.synexis.dto.CustomerUpdateDto;
import com.morphgen.synexis.dto.CustomerViewDto;
import com.morphgen.synexis.entity.Address;
import com.morphgen.synexis.entity.Customer;
import com.morphgen.synexis.entity.File;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.DocumentType;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.CustomerNotFoundException;
import com.morphgen.synexis.exception.FileNotFoundException;
import com.morphgen.synexis.exception.FileProcessingException;
import com.morphgen.synexis.exception.InvalidInputException;
import com.morphgen.synexis.repository.CustomerRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.CustomerService;
import com.morphgen.synexis.utils.DocumentUrlUtil;
import com.morphgen.synexis.utils.EntityDiffUtil;


@Service

public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ActivityLogService activityLogService;


    private File createFile(MultipartFile multipartFile, DocumentType docType, Customer customer) throws IOException {
        
        File file = new File();
        file.setFilename(multipartFile.getOriginalFilename());
        file.setFileType(multipartFile.getContentType());
        file.setFileSize(multipartFile.getSize());
        file.setFileData(multipartFile.getBytes());
        file.setDocumentType(docType);
        file.setCustomer(customer);
        return file;
    }

    @Override
    @Transactional
    public Customer createCustomer(CustomerDto customerDto) {

        if(customerDto.getCustomerEmail() == null || customerDto.getCustomerEmail().isEmpty()){
            throw new InvalidInputException("Customer email cannot be empty!");
        }
        
        Optional<Customer> existingCustomerEmail = customerRepo.findByCustomerEmail(customerDto.getCustomerEmail());
        if (existingCustomerEmail.isPresent()){
            throw new DataIntegrityViolationException("A Customer with the email " + customerDto.getCustomerEmail() + " already exists!");
        }

        Customer customer = new Customer();

        customer.setCustomerPrefix(customerDto.getCustomerPrefix());
        customer.setCustomerFirstName(customerDto.getCustomerFirstName());
        customer.setCustomerLastName(customerDto.getCustomerLastName());
        customer.setCustomerEmail(customerDto.getCustomerEmail());
        customer.setCustomerPhoneNumber(customerDto.getCustomerPhoneNumber());

        Address address = new Address();

        address.setAddressLine1(customerDto.getAddressLine1());
        address.setAddressLine2(customerDto.getAddressLine2());
        address.setCity(customerDto.getCity());
        address.setZipCode(customerDto.getZipCode());

        customer.setCustomerAddress(address);

        List<File> customerFiles = new ArrayList<>();

        try {
            if (customerDto.getBRC() != null && !customerDto.getBRC().isEmpty()) {
                customerFiles.add(createFile(customerDto.getBRC(), DocumentType.BRC, customer));
            }
            if (customerDto.getVAT() != null && !customerDto.getVAT().isEmpty()) {
                customerFiles.add(createFile(customerDto.getVAT(), DocumentType.VAT, customer));
            }
            if (customerDto.getSVAT() != null && !customerDto.getSVAT().isEmpty()) {
                customerFiles.add(createFile(customerDto.getSVAT(), DocumentType.SVAT, customer));
            }
        } catch (IOException e) {
            throw new FileProcessingException("Unable to process file. Please ensure the file is valid and try again!");
        }

        customer.setFiles(customerFiles);

        Customer newCustomer = customerRepo.save(customer);

        activityLogService.logActivity(
            "Customer", 
            newCustomer.getCustomerId(),
            newCustomer.getCustomerFirstName(),
            Action.CREATE, 
            "Created Customer: " + newCustomer.getCustomerPrefix() + " " + newCustomer.getCustomerFirstName() + " " + newCustomer.getCustomerLastName());
            
        return newCustomer;
    }

    @Override
    public List<CustomerTableViewDto> viewCustomerTable() {
        
        List<Customer> customers = customerRepo.findAllByOrderByCustomerIdDesc();

        List<CustomerTableViewDto> customerTableViewDtoList = customers.stream().map(customer ->{

            CustomerTableViewDto customerTableViewDto = new CustomerTableViewDto();

            customerTableViewDto.setCustomerId(customer.getCustomerId());
            customerTableViewDto.setCustomerName(customer.getCustomerPrefix() + " " + customer.getCustomerFirstName());
            customerTableViewDto.setCustomerPhoneNumber(customer.getCustomerPhoneNumber());
            customerTableViewDto.setCustomerEmail(customer.getCustomerEmail());
            customerTableViewDto.setCustomerStatus(customer.getCustomerStatus());

            return customerTableViewDto;
        }).collect(Collectors.toList());

        return customerTableViewDtoList;
    }

    @Override
    public List<CustomerSideDropViewDto> viewCustomerSideDrop() {
        
        List<Customer> customers = customerRepo.findAllByOrderByCustomerIdDesc();

        List<CustomerSideDropViewDto> customerSideDropViewDtoList = customers.stream().map(customer ->{

            CustomerSideDropViewDto customerSideDropViewDto = new CustomerSideDropViewDto();

            customerSideDropViewDto.setCustomerId(customer.getCustomerId());
            customerSideDropViewDto.setCustomerPrefix(customer.getCustomerPrefix());
            customerSideDropViewDto.setCustomerFirstName(customer.getCustomerFirstName());
            customerSideDropViewDto.setCustomerLastName(customer.getCustomerLastName());
            customerSideDropViewDto.setCustomerEmail(customer.getCustomerEmail());

            return customerSideDropViewDto;
        }).collect(Collectors.toList());

        return customerSideDropViewDtoList;
    }

    @Override
    public CustomerViewDto viewCustomerById(Long customerId) {
        
        Customer customer = customerRepo.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException("Customer ID: " + customerId + " is not found!"));

        CustomerViewDto customerViewDto = new CustomerViewDto();

        customerViewDto.setCustomerId(customer.getCustomerId());
        customerViewDto.setCustomerPrefix(customer.getCustomerPrefix());
        customerViewDto.setCustomerFirstName(customer.getCustomerFirstName());
        customerViewDto.setCustomerLastName(customer.getCustomerLastName());
        customerViewDto.setCustomerEmail(customer.getCustomerEmail());
        customerViewDto.setCustomerPhoneNumber(customer.getCustomerPhoneNumber());
        customerViewDto.setAddressLine1(customer.getCustomerAddress().getAddressLine1());
        customerViewDto.setAddressLine2(customer.getCustomerAddress().getAddressLine2());
        customerViewDto.setCity(customer.getCustomerAddress().getCity());
        customerViewDto.setZipCode(customer.getCustomerAddress().getZipCode());

        for (File file : customer.getFiles()) {
        String fileUrl = DocumentUrlUtil.constructCustomerDocumentUrl(
            customer.getCustomerId(), file.getDocumentType()
        );

        switch (file.getDocumentType()) {
            case BRC -> customerViewDto.setBRCDocUrl(fileUrl);
            case VAT -> customerViewDto.setVATDocUrl(fileUrl);
            case SVAT -> customerViewDto.setSVATDocUrl(fileUrl);
        }
    }

        return customerViewDto;
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long customerId, CustomerDto customerDto) {
        
        Customer customer = customerRepo.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException("Customer ID: " + customerId + " is not found!"));

        if(customerDto.getCustomerEmail() == null || customerDto.getCustomerEmail().isEmpty()){
            throw new InvalidInputException("Customer email cannot be empty!");
        }

        if (!customer.getCustomerEmail().equalsIgnoreCase(customerDto.getCustomerEmail())) {
            Optional<Customer> existingCustomerEmail = customerRepo.findByCustomerEmail(customerDto.getCustomerEmail());
            if (existingCustomerEmail.isPresent()) {
                throw new DataIntegrityViolationException("A Customer with the email " + customerDto.getCustomerEmail() + " already exists!");
            }
        }
        
        Map<DocumentType, byte[]> fileData = customer.getFiles().stream()
        .collect(Collectors.toMap(
        File::getDocumentType,
        File::getFileData
        ));

        CustomerUpdateDto existingCustomer = CustomerUpdateDto.builder()
        .customerId(customer.getCustomerId())
        .customerPrefix(customer.getCustomerPrefix())
        .customerFirstName(customer.getCustomerFirstName())
        .customerLastName(customer.getCustomerLastName())
        .customerEmail(customer.getCustomerEmail())
        .customerPhoneNumber(customer.getCustomerPhoneNumber())
        .addressLine1(customer.getCustomerAddress().getAddressLine1())
        .addressLine2(customer.getCustomerAddress().getAddressLine2())
        .city(customer.getCustomerAddress().getCity())
        .zipCode(customer.getCustomerAddress().getZipCode())
        .customerStatus(customer.getCustomerStatus())
        .fileBRC(fileData.getOrDefault(DocumentType.BRC, null))
        .fileVAT(fileData.getOrDefault(DocumentType.VAT, null))
        .fileSVAT(fileData.getOrDefault(DocumentType.SVAT, null))
        .build();

        customer.setCustomerPrefix(customerDto.getCustomerPrefix());
        customer.setCustomerFirstName(customerDto.getCustomerFirstName());
        customer.setCustomerLastName(customerDto.getCustomerLastName());

        List<File> updatedFiles = new ArrayList<>();

        try {
            if (customerDto.getBRC() != null && !customerDto.getBRC().isEmpty()) {
                updatedFiles.add(createFile(customerDto.getBRC(), DocumentType.BRC, customer));
            }
            else if(customerDto.getBRC() == null || customerDto.getBRC().isEmpty()){
                customer.getFiles().removeIf(file -> file.getDocumentType() == DocumentType.BRC);
            }
            if (customerDto.getVAT() != null && !customerDto.getVAT().isEmpty()) {
                updatedFiles.add(createFile(customerDto.getVAT(), DocumentType.VAT, customer));
            }
            else if(customerDto.getVAT() == null || customerDto.getVAT().isEmpty()){
                customer.getFiles().removeIf(file -> file.getDocumentType() == DocumentType.VAT);
            }
            if (customerDto.getSVAT() != null && !customerDto.getSVAT().isEmpty()) {
                updatedFiles.add(createFile(customerDto.getSVAT(), DocumentType.SVAT, customer));
            }
            else if(customerDto.getSVAT() == null || customerDto.getSVAT().isEmpty()){
                customer.getFiles().removeIf(file -> file.getDocumentType() == DocumentType.SVAT);
            }
        } 
        catch (IOException e) {
            throw new FileProcessingException("Unable to process file. Please ensure the file is valid and try again!");
        }

        customer.setFiles(updatedFiles);

        customer.setCustomerEmail(customerDto.getCustomerEmail());
        customer.setCustomerPhoneNumber(customerDto.getCustomerPhoneNumber());

        Address address = new Address();
        address.setAddressLine1(customerDto.getAddressLine1());
        address.setAddressLine2(customerDto.getAddressLine2());
        address.setCity(customerDto.getCity());
        address.setZipCode(customerDto.getZipCode());

        customer.setCustomerAddress(address);

        Customer updatedCustomer = customerRepo.save(customer);

        Map<DocumentType, byte[]> newFileData = updatedCustomer.getFiles().stream()
        .collect(Collectors.toMap(
        File::getDocumentType,
        File::getFileData
        ));

        CustomerUpdateDto newCustomer = CustomerUpdateDto.builder()
        .customerId(updatedCustomer.getCustomerId())
        .customerPrefix(updatedCustomer.getCustomerPrefix())
        .customerFirstName(updatedCustomer.getCustomerFirstName())
        .customerLastName(updatedCustomer.getCustomerLastName())
        .customerEmail(updatedCustomer.getCustomerEmail())
        .customerPhoneNumber(updatedCustomer.getCustomerPhoneNumber())
        .addressLine1(updatedCustomer.getCustomerAddress().getAddressLine1())
        .addressLine2(updatedCustomer.getCustomerAddress().getAddressLine2())
        .city(updatedCustomer.getCustomerAddress().getCity())
        .zipCode(updatedCustomer.getCustomerAddress().getZipCode())
        .customerStatus(updatedCustomer.getCustomerStatus())
        .fileBRC(newFileData.getOrDefault(DocumentType.BRC, null))
        .fileVAT(newFileData.getOrDefault(DocumentType.VAT, null))
        .fileSVAT(newFileData.getOrDefault(DocumentType.SVAT, null))
        .build();

        String changes = EntityDiffUtil.describeChanges(existingCustomer, newCustomer);

        activityLogService.logActivity(
            "Customer",
            updatedCustomer.getCustomerId(),
            updatedCustomer.getCustomerFirstName(),
            Action.UPDATE,
            changes.isBlank() ? "No changes detected" : changes);

        return updatedCustomer;
    }

    @Override
    public void deleteCustomer(Long customerId) {
        
        Customer customer = customerRepo.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException("Customer ID: " + customerId + " is not found!"));

        customer.setCustomerStatus(Status.INACTIVE);

        customerRepo.save(customer);

        activityLogService.logActivity(
            "Customer", 
            customer.getCustomerId(),
            customer.getCustomerFirstName(), 
            Action.DELETE, 
            "Deleted Customer: " + customer.getCustomerPrefix() + " " + customer.getCustomerFirstName() + " " + customer.getCustomerLastName());
    }

    @Override
    public File getCustomerFile(Long customerId, DocumentType documentType) {
        
        Customer customer = customerRepo.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException("Customer ID: " + customerId + " is not found!"));

        return customer.getFiles().stream()
            .filter(f -> f.getDocumentType() == documentType)
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException("Requested document not found for this customer"));
    }

    @Override
    public void reactivateCustomer(Long customerId) {
        
        Customer customer = customerRepo.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException("Customer ID: " + customerId + " is not found!"));

        if (customer.getCustomerStatus() == Status.ACTIVE){
            throw new DataIntegrityViolationException("Brand is already active!");
        }

        customer.setCustomerStatus(Status.ACTIVE);

        customerRepo.save(customer);

        activityLogService.logActivity(
            "Customer", 
            customer.getCustomerId(),
            customer.getCustomerFirstName(), 
            Action.REACTIVATE, 
            "Reactivated Customer: " + customer.getCustomerPrefix() + " " + customer.getCustomerFirstName() + " " + customer.getCustomerLastName());
    }
}
