package com.morphgen.synexis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.UnitDto;
import com.morphgen.synexis.service.UnitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/synexis/unit")
@CrossOrigin("*")

public class UnitController {
    
    private final UnitService unitService;

    public UnitController(UnitService unitService){
        this.unitService = unitService;
    }

    @PostMapping
    public ResponseEntity<String> createUnit(@RequestBody UnitDto unitDto) {
        
        if(unitDto.getUnitName() == null || unitDto.getUnitName().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Unit Name is Required!");
        }
        else if(unitDto.getUnitShortName() == null || unitDto.getUnitShortName().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Unit Short Name is Required!");
        }

        unitService.createUnit(unitDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Unit successfully created!");
    }
    

}
