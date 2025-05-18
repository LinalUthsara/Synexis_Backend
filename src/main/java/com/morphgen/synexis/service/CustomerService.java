package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CustomerDto;
import com.morphgen.synexis.dto.CustomerSideDropViewDto;
import com.morphgen.synexis.dto.CustomerTableViewDto;
import com.morphgen.synexis.dto.CustomerViewDto;
import com.morphgen.synexis.entity.Customer;
import com.morphgen.synexis.entity.File;
import com.morphgen.synexis.enums.DocumentType;

@Service

public interface CustomerService {
    
    Customer createCustomer(CustomerDto customerDto);

    List<CustomerTableViewDto> viewCustomerTable();
    List<CustomerSideDropViewDto> viewCustomerSideDrop();
    CustomerViewDto viewCustomerById(Long customerId);

    Customer updateCustomer(Long customerId, CustomerDto customerDto);
    
    void deleteCustomer(Long customerId);

    File getCustomerFile(Long customerId, DocumentType documentType);

}


