package com.morphgen.synexis.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.UnitDto;
import com.morphgen.synexis.dto.UnitSideDropViewDto;
import com.morphgen.synexis.dto.UnitTableViewDto;
import com.morphgen.synexis.dto.UnitViewDto;
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

    @GetMapping
    public ResponseEntity<List<UnitTableViewDto>> viewUnitTable() {
        
        List<UnitTableViewDto> unitTableViewDtoList = unitService.viewUnitTable();

        return ResponseEntity.status(HttpStatus.OK).body(unitTableViewDtoList);
    }

    @GetMapping("/sideDrop")
    public ResponseEntity<List<UnitSideDropViewDto>> viewUnitSideDrop() {
        
        List<UnitSideDropViewDto> unitSideDropViewDtoList = unitService.viewUnitSideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(unitSideDropViewDtoList);
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<UnitViewDto> viewUnitById(@PathVariable Long unitId){

        UnitViewDto unitViewDto = unitService.viewUnitById(unitId);

        return ResponseEntity.status(HttpStatus.OK).body(unitViewDto);
    }

}
