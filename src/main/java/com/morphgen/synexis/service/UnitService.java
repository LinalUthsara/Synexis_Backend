package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.UnitDto;
import com.morphgen.synexis.dto.UnitSideDropViewDto;
import com.morphgen.synexis.dto.UnitTableViewDto;
import com.morphgen.synexis.dto.UnitViewDto;
import com.morphgen.synexis.entity.Unit;

@Service

public interface UnitService {
    
    Unit createUnit(UnitDto unitDto);

    List<UnitTableViewDto> viewUnitTable();
    List<UnitSideDropViewDto> viewUnitSideDrop();
    UnitViewDto viewUnitById(Long unitId);

    Unit updateUnit(Long unitId, UnitDto unitDto);

    void deleteUnit(Long unitId);

}