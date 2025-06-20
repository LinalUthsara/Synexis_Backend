package com.morphgen.synexis.service;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CostEstimationDto;
import com.morphgen.synexis.dto.CostEstimationForBOQViewDto;
import com.morphgen.synexis.dto.CostEstimationTableViewDto;
import com.morphgen.synexis.dto.CostEstimationViewDto;
import com.morphgen.synexis.entity.CostEstimation;
import com.morphgen.synexis.enums.EstimationStatus;

@Service

public interface CostEstimationService {
    
    CostEstimation createEstimation(CostEstimationDto costEstimationDto);

    CostEstimationTableViewDto viewEstimationTableByInquiryId(Long inquiryId);
    CostEstimationTableViewDto viewEstimationApprovalTable(Long inquiryId);

    CostEstimationViewDto viewEstimationById(Long estimationId);
    
    CostEstimationForBOQViewDto viewEstimationForBOQ(Long jobId);

    CostEstimation updateEstimation(Long estimationId, CostEstimationDto costEstimationDto);

    CostEstimation handleEstimation(Long estimationId, EstimationStatus estimationStatus);
}
