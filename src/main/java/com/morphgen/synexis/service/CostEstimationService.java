package com.morphgen.synexis.service;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CostEstimationDto;
import com.morphgen.synexis.entity.CostEstimation;

@Service

public interface CostEstimationService {
    
    CostEstimation createEstimation(CostEstimationDto costEstimationDto);
}
