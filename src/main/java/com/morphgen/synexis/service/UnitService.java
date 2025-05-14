package com.morphgen.synexis.service;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.UnitDto;
import com.morphgen.synexis.entity.Unit;

@Service

public interface UnitService {
    
    Unit createUnit(UnitDto unitDto);

}