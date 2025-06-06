package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.dto.MaterialDropDownDto;
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
    public ResponseEntity<String> deleteMaterial(@PathVariable Long materialId){

        materialService.deleteMaterial(materialId);

        return ResponseEntity.status(HttpStatus.OK).body("Material successfully deleted!");
    }

    @GetMapping("/search")
    public ResponseEntity<List<MaterialDropDownDto>> viewMaterialDropDown(@RequestParam String searchMaterial){
        
        List<MaterialDropDownDto> materialDropDownDtoList = materialService.viewMaterialDropDown(searchMaterial);
        
        return ResponseEntity.status(HttpStatus.OK).body(materialDropDownDtoList);
    }

    @PatchMapping("/reactivate/{materialId}")
    public ResponseEntity<String> reactivateMaterial(@PathVariable Long materialId){

        materialService.reactivateMaterial(materialId);

        return ResponseEntity.status(HttpStatus.OK).body("Material successfully reactivated!");
    }

}
