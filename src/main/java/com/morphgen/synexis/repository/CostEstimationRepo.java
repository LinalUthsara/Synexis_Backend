package com.morphgen.synexis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morphgen.synexis.entity.CostEstimation;
import com.morphgen.synexis.enums.EstimationStatus;

@Repository

public interface CostEstimationRepo extends JpaRepository<CostEstimation, Long> {
    
    int countByInquiry_InquiryId(Long inquiryId);
    
    List<CostEstimation> findByInquiry_InquiryIdOrderByEstimationIdDesc(Long inquiryId);

    List<CostEstimation> findByInquiry_InquiryIdAndEstimationStatusNotOrderByEstimationIdDesc(Long inquiryId, EstimationStatus estimationStatus);
    
    boolean existsByInquiry_InquiryIdAndEstimationStatus(Long inquiryId, EstimationStatus status);


}
