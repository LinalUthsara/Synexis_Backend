package com.morphgen.synexis.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.UnitDto;
import com.morphgen.synexis.dto.UnitSideDropViewDto;
import com.morphgen.synexis.dto.UnitTableViewDto;
import com.morphgen.synexis.dto.UnitViewDto;
import com.morphgen.synexis.entity.Unit;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.exception.UnitNotFoundException;
import com.morphgen.synexis.repository.UnitRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.UnitService;

@Service 

public class UnitServiceImpl implements UnitService {
    
    @Autowired 
    private UnitRepo unitRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public Unit createUnit(UnitDto unitDto) {
        
        Optional<Unit> existingUnitByName = unitRepo.findByUnitName(unitDto.getUnitName());
        if(existingUnitByName.isPresent()){
            throw new DataIntegrityViolationException("A Unit with the name " + unitDto.getUnitName() + " already exists!");
        }
        Optional<Unit> existingUnitByShortName = unitRepo.findByUnitShortName(unitDto.getUnitShortName());
        if(existingUnitByShortName.isPresent()){
            throw new DataIntegrityViolationException("A Unit with the short name " + unitDto.getUnitShortName() + " already exists!");
        }
        
        Unit unit = new Unit();

        unit.setUnitName(unitDto.getUnitName());
        unit.setUnitShortName(unitDto.getUnitShortName());
        unit.setUnitAllowDecimal(unitDto.getUnitAllowDecimal());

        if(unitDto.getBaseUnitId() != null){
            Unit baseUnit = unitRepo.findById(unitDto.getBaseUnitId())
            .orElseThrow(() -> new UnitNotFoundException("Base Unit ID: " + unitDto.getBaseUnitId() + " is not found!"));

            unit.setBaseUnit(baseUnit);
            unit.setUnitConversionFactor(unitDto.getUnitConversionFactor());
        }
        else{
            unit.setBaseUnit(null);
            unit.setUnitConversionFactor(null);
        }

        Unit newUnit = unitRepo.save(unit);

        activityLogService.logActivity(
            "Unit", 
            newUnit.getUnitId(), 
            newUnit.getUnitName(), 
            Action.CREATE, 
            "Created Unit: " + newUnit.getUnitName());

            return newUnit;
    }

    @Override
    public List<UnitTableViewDto> viewUnitTable() {
        
        List<Unit> units = unitRepo.findAllByOrderByUnitIdDesc();

        List<UnitTableViewDto> unitTableViewDtoList = units.stream().map(unit ->{

            UnitTableViewDto unitTableViewDto = new UnitTableViewDto();

            unitTableViewDto.setUnitId(unit.getUnitId());
            unitTableViewDto.setUnitName(unit.getUnitName());
            unitTableViewDto.setUnitShortName(unit.getUnitShortName());
            unitTableViewDto.setUnitAllowDecimal(unit.getUnitAllowDecimal());
            unitTableViewDto.setUnitStatus(unit.getUnitStatus());

            return unitTableViewDto;
        }).collect(Collectors.toList());

        return unitTableViewDtoList;
    }

    @Override
    public List<UnitSideDropViewDto> viewUnitSideDrop() {
        
        List<Unit> units = unitRepo.findAllByOrderByUnitIdDesc();

        List<UnitSideDropViewDto> unitSideDropViewDtoList = units.stream().map(unit ->{

            UnitSideDropViewDto unitSideDropViewDto = new UnitSideDropViewDto();

            unitSideDropViewDto.setUnitId(unit.getUnitId());
            unitSideDropViewDto.setUnitName(unit.getUnitName());

            return unitSideDropViewDto;
        }).collect(Collectors.toList());

        return unitSideDropViewDtoList;
    }

    @Override
    public UnitViewDto viewUnitById(Long unitId) {
        
        Unit unit = unitRepo.findById(unitId)
        .orElseThrow(() -> new UnitNotFoundException("Unit ID: " + unitId + " is not found!"));

        UnitViewDto unitViewDto = new UnitViewDto();

        unitViewDto.setUnitId(unitId);
        unitViewDto.setUnitName(unit.getUnitName());
        unitViewDto.setUnitShortName(unit.getUnitShortName());
        unitViewDto.setUnitAllowDecimal(unit.getUnitAllowDecimal());
        unitViewDto.setUnitStatus(unit.getUnitStatus());

        return unitViewDto;
    }

}
