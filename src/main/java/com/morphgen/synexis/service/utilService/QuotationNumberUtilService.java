package com.morphgen.synexis.service.utilService;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.repository.InquiryRepo;

@Service

public class QuotationNumberUtilService {
    
    @Autowired
    private InquiryRepo inquiryRepo;

    public String generateQuotationNumber() {
        LocalDate today = LocalDate.now();
        String prefix = getQuotationNumberPrefix(today);
        long count = inquiryRepo.countByQuotationNumberStartingWith(prefix) + 1;
        return formatQuotationNumber(prefix, count);
    }

    private String getQuotationNumberPrefix(LocalDate date) {
        String year = String.valueOf(date.getYear()).substring(2);
        String month = String.format("%02d", date.getMonthValue());
        return String.format("PEQ/%s/%s/", year, month);
    }

    private String formatQuotationNumber(String prefix, long countForMonth) {
        String suffix = String.format("%04d", countForMonth);
        return prefix + suffix;
    }
    
}
