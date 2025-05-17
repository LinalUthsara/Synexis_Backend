package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.MaterialDto;
import com.morphgen.synexis.dto.MaterialSideDropViewDto;
import com.morphgen.synexis.dto.MaterialTableViewDto;
import com.morphgen.synexis.dto.MaterialViewDto;
import com.morphgen.synexis.service.MaterialService;

@RestController
@RequestMapping("api/synexis/material")
@CrossOrigin("*")

public class MaterialController {
    
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    public ResponseEntity<String> createMaterial(@ModelAttribute MaterialDto materialDto) throws IOException{

        if (materialDto.getMaterialName() == null || materialDto.getMaterialName().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Material Name is Required!");
        }
        else if (materialDto.getMaterialSKU() == null || materialDto.getMaterialSKU().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Material SKU is Required!");
        }
        else if (materialDto.getAlertQuantity() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Alert Quantity is Required!");
        }
        else if (materialDto.getMaterialType() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Material Type is Required!");
        }
        else if (materialDto.getMaterialInventoryType() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Inventory Type is Required!");
        }

        materialService.createMaterial(materialDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Material successfully created!");
    }

    @GetMapping
    public ResponseEntity<List<MaterialTableViewDto>> viewMaterialable(){
        
        List<MaterialTableViewDto> materialTableViewDtoList = materialService.viewMaterialTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(materialTableViewDtoList);
    }

    @GetMapping("/sideDrop")
    public ResponseEntity<List<MaterialSideDropViewDto>> viewMaterialSideDrop(){
        
        List<MaterialSideDropViewDto> materialSideDropViewDtoList = materialService.viewMaterialSideDrop();
        
        return ResponseEntity.status(HttpStatus.OK).body(materialSideDropViewDtoList);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<MaterialViewDto> viewMaterialById(@PathVariable Long materialId){

        MaterialViewDto materialViewDto = materialService.viewMaterialById(materialId);

        return ResponseEntity.status(HttpStatus.OK).body(materialViewDto);
    }

    @GetMapping("/image/{materialId}")
    public ResponseEntity<byte[]> getMaterialImage(@PathVariable Long materialId) {
        return materialService.viewMaterialImage(materialId);
    }

    @PutMapping("/{materialId}")
    public ResponseEntity<String> updateMaterial(@PathVariable Long materialId, @ModelAttribute MaterialDto materialDto){
        
        materialService.updateMaterial(materialId, materialDto);

        return ResponseEntity.status(HttpStatus.OK).body("Material successfully updated!");
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long materialId){

        materialService.deleteMaterial(materialId);

        return ResponseEntity.status(HttpStatus.OK).body("Material successfully deleted!");
    }

}
