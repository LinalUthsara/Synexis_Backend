package com.morphgen.synexis.service.serviceImpl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morphgen.synexis.dto.InquiryDto;
import com.morphgen.synexis.dto.InquirySideDropViewDto;
import com.morphgen.synexis.dto.InquiryTableViewDto;
import com.morphgen.synexis.dto.InquiryUpdateDto;
import com.morphgen.synexis.dto.InquiryViewDto;
import com.morphgen.synexis.entity.Customer;
import com.morphgen.synexis.entity.Employee;
import com.morphgen.synexis.entity.Inquiry;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.CustomerNotFoundException;
import com.morphgen.synexis.exception.EmployeeNotFoundException;
import com.morphgen.synexis.exception.InquiryNotFoundException;
import com.morphgen.synexis.exception.InvalidInputException;
import com.morphgen.synexis.repository.CustomerRepo;
import com.morphgen.synexis.repository.EmployeeRepo;
import com.morphgen.synexis.repository.InquiryRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.InquiryService;
import com.morphgen.synexis.service.utilService.QuotationNumberUtilService;
import com.morphgen.synexis.utils.EntityDiffUtil;

@Service

public class InquiryServiceImpl implements InquiryService {
    
    @Autowired
    private InquiryRepo inquiryRepo;

    @Autowired
    private QuotationNumberUtilService quotationNumberUtilService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    @Transactional
    public Inquiry createInquiry(InquiryDto inquiryDto) {

        if(inquiryDto.getProjectName() == null || inquiryDto.getProjectName().isEmpty()){
            throw new InvalidInputException("Project Name cannot be empty!");
        }
        else if(inquiryDto.getProjectReturnDate() == null){
            throw new InvalidInputException("Project Return Date cannot be empty!");
        }
        else if(inquiryDto.getInquiryType() == null){
            throw new InvalidInputException("Inquiry Type cannot be empty!");
        }
        else if(inquiryDto.getProjectType() == null){
            throw new InvalidInputException("Project Type cannot be empty!");
        }
        else if(inquiryDto.getCustomerId() == null){
            throw new InvalidInputException("Customer cannot be empty!");
        }
        else if(inquiryDto.getEstimatorId() == null){
            throw new InvalidInputException("Estimator cannot be empty!");
        }
        else if(inquiryDto.getSalesPersonId() == null){
            throw new InvalidInputException("Sales Person cannot be empty!");
        }

        Optional<Inquiry> existingProjectName = inquiryRepo.findByProjectName(inquiryDto.getProjectName());
        if (existingProjectName.isPresent()) {
            throw new DataIntegrityViolationException("An Inquiry with the name " + inquiryDto.getProjectName() + " already exists!");
        }

        Inquiry inquiry = new Inquiry();

        inquiry.setProjectName(inquiryDto.getProjectName());
        inquiry.setProjectReturnDate(inquiryDto.getProjectReturnDate());
        inquiry.setInquiryType(inquiryDto.getInquiryType());
        inquiry.setProjectType(inquiryDto.getProjectType());

        if (inquiryDto.getCustomerId() != null){
            Customer customer = customerRepo.findById(inquiryDto.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException("Customer ID: " + inquiryDto.getCustomerId() + " is not found!"));

            inquiry.setCustomer(customer);
        }

        if (inquiryDto.getEstimatorId() != null){
            Employee estimator = employeeRepo.findById(inquiryDto.getEstimatorId())
            .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + inquiryDto.getEstimatorId() + " is not found!"));

            inquiry.setEstimator(estimator);
        }

        if (inquiryDto.getSalesPersonId() != null){
            Employee salesPerson = employeeRepo.findById(inquiryDto.getSalesPersonId())
            .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + inquiryDto.getEstimatorId() + " is not found!"));

            inquiry.setSalesPerson(salesPerson);
        }

        inquiry.setQuotationNumber(quotationNumberUtilService.generateQuotationNumber());

        Inquiry newInquiry = inquiryRepo.save(inquiry);

        activityLogService.logActivity(
            "Inquiry", 
            newInquiry.getInquiryId(),
            newInquiry.getProjectName(),
            Action.CREATE, 
            "Created Inquiry: " + newInquiry.getProjectName());

            return newInquiry;
    }

    @Override
    public List<InquiryTableViewDto> viewInquiryTable() {

        List<Inquiry> inquiries = inquiryRepo.findAllByOrderByInquiryIdDesc();

        List<InquiryTableViewDto> inquiryTableViewDtoList = inquiries.stream().map(inquiry ->{

            InquiryTableViewDto inquiryTableViewDto = new InquiryTableViewDto();

            inquiryTableViewDto.setInquiryId(inquiry.getInquiryId());
            inquiryTableViewDto.setQuotationNumber(inquiry.getQuotationNumber());
            inquiryTableViewDto.setProjectName(inquiry.getProjectName());
            inquiryTableViewDto.setCustomerName(inquiry.getCustomer().getCustomerPrefix() + " " + inquiry.getCustomer().getCustomerFirstName() + " " + inquiry.getCustomer().getCustomerLastName());
            inquiryTableViewDto.setInquiryStatus(inquiry.getInquiryStatus());

            return inquiryTableViewDto;
        }).collect(Collectors.toList());

        return inquiryTableViewDtoList;
    }

    @Override
    public List<InquirySideDropViewDto> viewInquirySideDrop() {
        
        List<Inquiry> inquiries = inquiryRepo.findAllByOrderByInquiryIdDesc();

        List<InquirySideDropViewDto> inquirySideDropViewDtoList = inquiries.stream().map(inquiry ->{

            InquirySideDropViewDto inquirySideDropViewDto = new InquirySideDropViewDto();

            inquirySideDropViewDto.setInquiryId(inquiry.getInquiryId());
            inquirySideDropViewDto.setQuotationNumber(inquiry.getQuotationNumber());
            inquirySideDropViewDto.setProjectName(inquiry.getProjectName());

            return inquirySideDropViewDto;
        }).collect(Collectors.toList());

        return inquirySideDropViewDtoList;
    }

    @Override
    public InquiryViewDto viewInquiryById(Long inquiryId) {
        
        Inquiry inquiry = inquiryRepo.findById(inquiryId)
        .orElseThrow(() -> new InquiryNotFoundException("Inquiry ID: " + inquiryId + " is not found!"));

        InquiryViewDto inquiryViewDto = new InquiryViewDto();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

        inquiryViewDto.setInquiryId(inquiry.getInquiryId());
        inquiryViewDto.setQuotationNumber(inquiry.getQuotationNumber());
        inquiryViewDto.setProjectName(inquiry.getProjectName());
        inquiryViewDto.setProjectReturnDate(inquiry.getProjectReturnDate().format(formatter));
        inquiryViewDto.setInquiryType(inquiry.getInquiryType());
        inquiryViewDto.setProjectType(inquiry.getProjectType());
        inquiryViewDto.setCustomerName(inquiry.getCustomer().getCustomerPrefix() + " " + inquiry.getCustomer().getCustomerFirstName() + " " + inquiry.getCustomer().getCustomerLastName());
        inquiryViewDto.setEstimatorName(inquiry.getEstimator().getEmployeePrefix() + " " + inquiry.getEstimator().getEmployeeFirstName() + " " + inquiry.getEstimator().getEmployeeLastName());
        inquiryViewDto.setSalesPersonName(inquiry.getSalesPerson().getEmployeePrefix() + " " + inquiry.getSalesPerson().getEmployeeFirstName() + " " + inquiry.getSalesPerson().getEmployeeLastName());
        inquiryViewDto.setInquiryStatus(inquiry.getInquiryStatus());

        return inquiryViewDto;
    }

    @Override
    @Transactional
    public Inquiry updateInquiry(Long inquiryId, InquiryDto inquiryDto) {
        
        if(inquiryDto.getProjectName() == null || inquiryDto.getProjectName().isEmpty()){
            throw new InvalidInputException("Project Name cannot be empty!");
        }
        else if(inquiryDto.getProjectReturnDate() == null){
            throw new InvalidInputException("Project Return Date cannot be empty!");
        }
        else if(inquiryDto.getInquiryType() == null){
            throw new InvalidInputException("Inquiry Type cannot be empty!");
        }
        else if(inquiryDto.getProjectType() == null){
            throw new InvalidInputException("Project Type cannot be empty!");
        }
        else if(inquiryDto.getCustomerId() == null){
            throw new InvalidInputException("Customer cannot be empty!");
        }
        else if(inquiryDto.getEstimatorId() == null){
            throw new InvalidInputException("Estimator cannot be empty!");
        }
        else if(inquiryDto.getSalesPersonId() == null){
            throw new InvalidInputException("Sales Person cannot be empty!");
        }

        Inquiry inquiry = inquiryRepo.findById(inquiryId)
        .orElseThrow(() -> new InquiryNotFoundException("Inquiry ID: " + inquiryId + " is not found!"));

        if (!inquiry.getProjectName().equalsIgnoreCase(inquiryDto.getProjectName())){

            Optional<Inquiry> existingProjectName = inquiryRepo.findByProjectName(inquiryDto.getProjectName());
            if (existingProjectName.isPresent()) {
                throw new DataIntegrityViolationException("An Inquiry with the name " + inquiryDto.getProjectName() + " already exists!");
            }
        }

        InquiryUpdateDto existingInquiry = InquiryUpdateDto.builder()
        .projectName(inquiry.getProjectName())
        .projectReturnDate(inquiry.getProjectReturnDate())
        .projectType(inquiry.getProjectType())
        .customerName(inquiry.getCustomer().getCustomerPrefix() + " " + inquiry.getCustomer().getCustomerFirstName() + " " + inquiry.getCustomer().getCustomerLastName())
        .estimatorName(inquiry.getEstimator().getEmployeePrefix() + " " + inquiry.getEstimator().getEmployeeFirstName() + " " + inquiry.getEstimator().getEmployeeLastName())
        .salesPersonName(inquiry.getSalesPerson().getEmployeePrefix() + " " + inquiry.getSalesPerson().getEmployeeFirstName() + " " + inquiry.getSalesPerson().getEmployeeLastName())
        .inquiryType(inquiry.getInquiryType())
        .build();

        inquiry.setProjectName(inquiryDto.getProjectName());
        inquiry.setProjectReturnDate(inquiryDto.getProjectReturnDate());
        inquiry.setInquiryType(inquiryDto.getInquiryType());
        inquiry.setProjectType(inquiryDto.getProjectType());

        if (inquiryDto.getCustomerId() != null){
            Customer customer = customerRepo.findById(inquiryDto.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException("Customer ID: " + inquiryDto.getCustomerId() + " is not found!"));

            inquiry.setCustomer(customer);
        }

        if (inquiryDto.getEstimatorId() != null){
            Employee estimator = employeeRepo.findById(inquiryDto.getEstimatorId())
            .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + inquiryDto.getEstimatorId() + " is not found!"));

            inquiry.setEstimator(estimator);
        }

        if (inquiryDto.getSalesPersonId() != null){
            Employee salesPerson = employeeRepo.findById(inquiryDto.getSalesPersonId())
            .orElseThrow(() -> new EmployeeNotFoundException("Employee ID: " + inquiryDto.getEstimatorId() + " is not found!"));

            inquiry.setSalesPerson(salesPerson);
        }

        Inquiry updatedInquiry = inquiryRepo.save(inquiry);

        InquiryUpdateDto newInquiry = InquiryUpdateDto.builder()
        .projectName(updatedInquiry.getProjectName())
        .projectReturnDate(updatedInquiry.getProjectReturnDate())
        .projectType(updatedInquiry.getProjectType())
        .customerName(updatedInquiry.getCustomer().getCustomerPrefix() + " " + updatedInquiry.getCustomer().getCustomerFirstName() + " " + updatedInquiry.getCustomer().getCustomerLastName())
        .estimatorName(updatedInquiry.getEstimator().getEmployeePrefix() + " " + updatedInquiry.getEstimator().getEmployeeFirstName() + " " + updatedInquiry.getEstimator().getEmployeeLastName())
        .salesPersonName(updatedInquiry.getSalesPerson().getEmployeePrefix() + " " + updatedInquiry.getSalesPerson().getEmployeeFirstName() + " " + updatedInquiry.getSalesPerson().getEmployeeLastName())
        .inquiryType(updatedInquiry.getInquiryType())
        .build();

        String changes = EntityDiffUtil.describeChanges(existingInquiry, newInquiry);

        activityLogService.logActivity(
            "Inquiry", 
            updatedInquiry.getInquiryId(),
            updatedInquiry.getProjectName(),
            Action.UPDATE, 
            changes.isBlank() ? "No changes detected" : changes);

            return updatedInquiry;
        
    }

    @Override
    public void deleteInquiry(Long inquiryId) {
        
        Inquiry inquiry = inquiryRepo.findById(inquiryId)
        .orElseThrow(() -> new InquiryNotFoundException("Customer ID: " + inquiryId + " is not found!"));

        inquiry.setInquiryStatus(Status.INACTIVE);

        inquiryRepo.save(inquiry);

        activityLogService.logActivity(
            "Inquiry", 
            inquiryId, 
            inquiry.getProjectName(),
            Action.DELETE, 
            "Deleted Inquiry: " + inquiry.getProjectName());
    }
    
}
