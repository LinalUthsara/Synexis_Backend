package com.morphgen.synexis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.morphgen.synexis.dto.BrandDropDownDto;
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
    @PreAuthorize("hasAuthority('BRAND_CREATE')")
    public ResponseEntity<String> createBrand(@ModelAttribute BrandDto brandDto) throws IOException {

        brandService.createBrand(brandDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("New Brand successfully created!");
    }

    @GetMapping("/image/{brandId}")
    @PreAuthorize("hasAuthority('BRAND_VIEW')")
    public ResponseEntity<byte[]> viewBrandImage(@PathVariable Long brandId) {
        
        return brandService.viewBrandImage(brandId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BRAND_VIEW')")
    public ResponseEntity<List<BrandTableViewDto>> viewBrandTable(){
        
        List<BrandTableViewDto> brandTableViewDtoList = brandService.viewBrandTable();
        
        return ResponseEntity.status(HttpStatus.OK).body(brandTableViewDtoList);
    }

    @GetMapping("/sideDrop")
    @PreAuthorize("hasAuthority('BRAND_VIEW')")
    public ResponseEntity<List<BrandSideDropViewDto>> viewBrandSideDrop(){

        List<BrandSideDropViewDto> brandSideDropViewDtoList = brandService.viewBrandSideDrop();

        return ResponseEntity.status(HttpStatus.OK).body(brandSideDropViewDtoList);

    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('MATERIAL_CREATE')")
    public ResponseEntity<List<BrandDropDownDto>> brandDropDown(@RequestParam String searchBrand){

        List<BrandDropDownDto> brandDropDownDtoList = brandService.brandDropDown(searchBrand);

        return ResponseEntity.status(HttpStatus.OK).body(brandDropDownDtoList);

    }

    @GetMapping("/{brandId}")
    @PreAuthorize("hasAuthority('BRAND_VIEW')")
    public ResponseEntity<BrandViewDto> viewBrandById(@PathVariable Long brandId){

        BrandViewDto brandViewDto = brandService.viewBrandById(brandId);

        return ResponseEntity.status(HttpStatus.OK).body(brandViewDto);
    }

    @PutMapping("/{brandId}")
    @PreAuthorize("hasAuthority('BRAND_UPDATE')")
    public ResponseEntity<String> updateBrand(@PathVariable Long brandId, @ModelAttribute BrandDto brandDto){
        
        brandService.updateBrand(brandId, brandDto);

        return ResponseEntity.status(HttpStatus.OK).body("Brand successfully updated!");
    }

    @DeleteMapping("/{brandId}")
    @PreAuthorize("hasAuthority('BRAND_DELETE')")
    public ResponseEntity<String> deleteBrand(@PathVariable Long brandId){

        brandService.deleteBrand(brandId);

        return ResponseEntity.status(HttpStatus.OK).body("Brand successfully deleted!");
    }

    @PatchMapping("/reactivate/{brandId}")
    @PreAuthorize("hasAuthority('BRAND_REACTIVATE')")
    public ResponseEntity<String> reactivateBrand(@PathVariable Long brandId){

        brandService.reactivateBrand(brandId);

        return ResponseEntity.status(HttpStatus.OK).body("Brand successfully reactivated!");
    }

}
