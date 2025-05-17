package com.morphgen.synexis.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.MaterialDto;
import com.morphgen.synexis.dto.MaterialSideDropViewDto;
import com.morphgen.synexis.dto.MaterialTableViewDto;
import com.morphgen.synexis.dto.MaterialViewDto;
import com.morphgen.synexis.entity.Material;

@Service

public interface MaterialService {
    
    Material createMaterial(MaterialDto materialDto);

    ResponseEntity<byte[]> viewMaterialImage(Long materialId);

    List<MaterialTableViewDto> viewMaterialTable();
    List<MaterialSideDropViewDto> viewMaterialSideDrop();
    MaterialViewDto viewMaterialById(Long materialId);

    Material updateMaterial(Long materialId, MaterialDto materialDto);

    void deleteMaterial(Long materialId);
    
}
    // List<BrandDropDownDto> brandDropDown();

    
    // void deleteBrand(Long brandId);