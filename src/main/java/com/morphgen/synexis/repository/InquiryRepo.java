package com.morphgen.synexis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.Inquiry;

@Repository

public interface InquiryRepo extends JpaRepository<Inquiry, Long> {
    
}
