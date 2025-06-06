package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.BrandDropDownDto;
import com.morphgen.synexis.dto.BrandDto;
import com.morphgen.synexis.dto.BrandSideDropViewDto;
import com.morphgen.synexis.dto.BrandTableViewDto;
import com.morphgen.synexis.dto.BrandViewDto;
import com.morphgen.synexis.entity.Brand;

@Service

public interface BrandService {
    
    Brand createBrand(BrandDto brandDto);

    ResponseEntity<byte[]> viewBrandImage(Long brandId);
    List<BrandTableViewDto> viewBrandTable();
    List<BrandSideDropViewDto> viewBrandSideDrop();
    BrandViewDto viewBrandById(Long brandId);

    List<BrandDropDownDto> brandDropDown(String searchBrand);

    Brand updateBrand(Long brandId, BrandDto brandDto);
    
    void deleteBrand(Long brandId);

    void reactivateBrand(Long brandId);

}
