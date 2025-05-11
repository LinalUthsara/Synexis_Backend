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

import com.morphgen.synexis.dto.BrandDto;
import com.morphgen.synexis.dto.BrandSideDropViewDto;
import com.morphgen.synexis.dto.BrandTableViewDto;
import com.morphgen.synexis.dto.BrandViewDto;
import com.morphgen.synexis.service.BrandService;

@RestController
@RequestMapping("api/synexis/brand")
@CrossOrigin("*")

public class BrandController {
    

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<String> createBrand(@ModelAttribute BrandDto brandDto) throws IOException {
        
        if(brandDto.getBrandName() == null || brandDto.getBrandName().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Brand Name is Required!");
        }
        else if(brandDto.getBrandCountry() == null || brandDto.getBrandCountry().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation Failed: Brand Country is Required!");
        }

        brandService.createBrand(brandDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Brand successfully created!");
    }


    @GetMapping("/image/{brandId}")
    public ResponseEntity<byte[]> getBrandImage(@PathVariable Long brandId) {
        return brandService.viewBrandImage(brandId);
    }

    @GetMapping
    public ResponseEntity<List<BrandTableViewDto>> viewBrandTable(){
        
        List<BrandTableViewDto> brandTableViewDtoList = brandService.viewBrandTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(brandTableViewDtoList);
    }

    @GetMapping("/sideDrop")
    public ResponseEntity<List<BrandSideDropViewDto>> viewBrandSideDrop(){

        List<BrandSideDropViewDto> brandSideDropViewDtoList = brandService.viewBrandSideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(brandSideDropViewDtoList);

    }

    @GetMapping("/{brandId}")
    public ResponseEntity<BrandViewDto> viewBrandById(@PathVariable Long brandId){

        BrandViewDto brandViewDto = brandService.viewBrandById(brandId);

        return ResponseEntity.status(HttpStatus.OK).body(brandViewDto);
    }


    @PutMapping("/{brandId}")
    public ResponseEntity<String> updateBrand(@PathVariable Long brandId, @ModelAttribute BrandDto brandDto){
        
        brandService.updateBrand(brandId, brandDto);

        return ResponseEntity.status(HttpStatus.OK).body("Brand successfully updated!");
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long brandId){

        brandService.deleteBrand(brandId);

        return ResponseEntity.status(HttpStatus.OK).body("Brand successfully deleted!");
    }

}
