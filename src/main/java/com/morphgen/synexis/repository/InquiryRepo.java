package com.morphgen.synexis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Inquiry;

@Repository

public interface InquiryRepo extends JpaRepository<Inquiry, Long> {
    
    Optional<Inquiry> findByProjectName(String projectName);

    long countByQuotationNumberStartingWith(String prefix);
    
    List<Inquiry> findAllByOrderByInquiryIdDesc();
    
}
