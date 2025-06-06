package com.morphgen.synexis.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.BaseUnitDropDownDto;
import com.morphgen.synexis.dto.UnitDropDownDto;
import com.morphgen.synexis.dto.UnitDto;
import com.morphgen.synexis.dto.UnitSideDropViewDto;
import com.morphgen.synexis.dto.UnitTableViewDto;
import com.morphgen.synexis.dto.UnitViewDto;
import com.morphgen.synexis.service.UnitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/baseUnitDropDown")
    public ResponseEntity<List<BaseUnitDropDownDto>> baseUnitDropDown(@RequestParam String searchUnit) {
        
        List<BaseUnitDropDownDto> baseUnitDropDownDtoList = unitService.baseUnitDropDown(searchUnit);

        return ResponseEntity.status(HttpStatus.OK).body(baseUnitDropDownDtoList);
    }

    @GetMapping("/otherUnitDropDown/{baseUnitId}")
    public ResponseEntity<List<UnitDropDownDto>> otherUnitDropDown(@PathVariable Long baseUnitId, @RequestParam String searchUnit) {
        
        List<UnitDropDownDto> unitDropDownDtoList = unitService.otherUnitDropDown(baseUnitId, searchUnit);

        return ResponseEntity.status(HttpStatus.OK).body(unitDropDownDtoList);
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<UnitViewDto> viewUnitById(@PathVariable Long unitId){

        UnitViewDto unitViewDto = unitService.viewUnitById(unitId);

        return ResponseEntity.status(HttpStatus.OK).body(unitViewDto);
    }

    @PutMapping("/{unitId}")
    public ResponseEntity<String> updateUnit(@PathVariable Long unitId, @RequestBody UnitDto unitDto){
        
        unitService.updateUnit(unitId, unitDto);

        return ResponseEntity.status(HttpStatus.OK).body("Unit successfully updated!");
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<String> deleteUnit(@PathVariable Long unitId){

        unitService.deleteUnit(unitId);

        return ResponseEntity.status(HttpStatus.OK).body("Unit successfully deleted!");
    }

    @PatchMapping("/reactivate/{unitId}")
    public ResponseEntity<String> reactivateUnit(@PathVariable Long unitId){

        unitService.reactivateUnit(unitId);

        return ResponseEntity.status(HttpStatus.OK).body("Unit successfully reactivated!");
    }

}
