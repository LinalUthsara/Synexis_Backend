package com.morphgen.synexis.service;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CostEstimationDto;
import com.morphgen.synexis.dto.CostEstimationTableViewDto;
import com.morphgen.synexis.dto.CostEstimationViewDto;
import com.morphgen.synexis.entity.CostEstimation;

@Service

public interface CostEstimationService {
    
    CostEstimation createEstimation(CostEstimationDto costEstimationDto);

    CostEstimationTableViewDto viewEstimationTableByInquiryId(Long inquiryId);
    CostEstimationViewDto viewEstimationById(Long estimationId);

    CostEstimation updateEstimation(Long estimationId, CostEstimationDto costEstimationDto);
}
