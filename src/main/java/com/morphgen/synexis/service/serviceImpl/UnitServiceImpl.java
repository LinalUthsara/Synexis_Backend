package com.morphgen.synexis.service.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.UnitDto;
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

}
