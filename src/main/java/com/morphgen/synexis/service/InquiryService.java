package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.InquiryDto;
import com.morphgen.synexis.dto.InquirySideDropViewDto;
import com.morphgen.synexis.dto.InquiryTableViewDto;
import com.morphgen.synexis.dto.InquiryViewDto;
import com.morphgen.synexis.entity.Inquiry;

@Service

public interface InquiryService {
    
    Inquiry createInquiry(InquiryDto inquiryDto);

    List<InquiryTableViewDto> viewInquiryTable();
    List<InquirySideDropViewDto> viewInquirySideDrop();
    InquiryViewDto viewInquiryById(Long inquiryId);

    Inquiry updateInquiry(Long inquiryId, InquiryDto inquiryDto);

    void deleteInquiry(Long inquiryId);

    void reactivateInquiry(Long inquiryId);
    
}
