package com.morphgen.synexis.service.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morphgen.synexis.dto.MaterialDto;
import com.morphgen.synexis.dto.MaterialSideDropViewDto;
import com.morphgen.synexis.dto.MaterialTableViewDto;
import com.morphgen.synexis.dto.MaterialUpdateDto;
import com.morphgen.synexis.dto.MaterialViewDto;
import com.morphgen.synexis.entity.Brand;
import com.morphgen.synexis.entity.Category;
import com.morphgen.synexis.entity.Material;
import com.morphgen.synexis.entity.Unit;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.BrandNotFoundException;
import com.morphgen.synexis.exception.CategoryNotFoundException;
import com.morphgen.synexis.exception.MaterialNotFoundException;
import com.morphgen.synexis.exception.UnitNotFoundException;
import com.morphgen.synexis.repository.BrandRepo;
import com.morphgen.synexis.repository.CategoryRepo;
import com.morphgen.synexis.repository.MaterialRepo;
import com.morphgen.synexis.repository.UnitRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.MaterialService;
import com.morphgen.synexis.utils.BarcodeGenUtil;
import com.morphgen.synexis.utils.EntityDiffUtil;
import com.morphgen.synexis.utils.ImageUrlUtil;

@Service

public class MaterialServiceImpl implements MaterialService {
    
    @Autowired
    private MaterialRepo materialRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private BrandRepo brandRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UnitRepo unitRepo;

    @Override
    @Transactional
    public Material createMaterial(MaterialDto materialDto) {
        
        Optional<Material> existingMaterialName = materialRepo.findByMaterialName(materialDto.getMaterialName());
        if (existingMaterialName.isPresent()) {
            throw new DataIntegrityViolationException("A Material with the name " + materialDto.getMaterialName() + " already exists!");
        }
        Optional<Material> existingMaterialSKU = materialRepo.findByMaterialSKU(materialDto.getMaterialSKU());
        if (existingMaterialSKU.isPresent()) {
            throw new DataIntegrityViolationException("A Material with the SKU " + materialDto.getMaterialSKU() + " already exists!");
        }

        Material material = new Material();

        material.setMaterialName(materialDto.getMaterialName());
        material.setMaterialDescription(materialDto.getMaterialDescription());
        material.setMaterialPartNumber(materialDto.getMaterialPartNumber());
        material.setMaterialSKU(materialDto.getMaterialSKU());

        try{
            if(materialDto.getMaterialImage() != null && !materialDto.getMaterialImage().isEmpty()){
                material.setMaterialImage(materialDto.getMaterialImage().getBytes());
            }
        }
        catch(IOException e){
            throw new IllegalArgumentException("Failed to process image file!");
        }

        material.setMaterialMarketPrice(materialDto.getMaterialMarketPrice());
        material.setMaterialPurchasePrice(materialDto.getMaterialPurchasePrice());
        material.setAlertQuantity(materialDto.getAlertQuantity());
        material.setMaterialForUse(materialDto.getMaterialForUse());
        material.setMaterialType(materialDto.getMaterialType());
        material.setMaterialInventoryType(materialDto.getMaterialInventoryType());

        if (materialDto.getBrandId() != null){
            Brand brand = brandRepo.findById(materialDto.getBrandId())
            .orElseThrow(() -> new BrandNotFoundException("Brand ID: " + materialDto.getBrandId() + " is not found!"));

            material.setBrand(brand);
        }

        if (materialDto.getCategoryId() != null){
            Category category = categoryRepo.findById(materialDto.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + materialDto.getCategoryId() + " is not found!"));

            material.setCategory(category);
        }

        if (materialDto.getSubCategoryId() != null){
            Category subCategory = categoryRepo.findById(materialDto.getSubCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("Sub - category ID: " + materialDto.getSubCategoryId() + " is not found!"));

            material.setSubCategory(subCategory);
        }        

        if (materialDto.getBaseUnitId() != null){
            Unit baseUnit = unitRepo.findById(materialDto.getBaseUnitId())
            .orElseThrow(() -> new UnitNotFoundException("Unit ID: " + materialDto.getBaseUnitId() + " is not found!"));

            material.setBaseUnit(baseUnit);
        }  

        if (materialDto.getOtherUnitId() != null){
            Unit otherUnit = unitRepo.findById(materialDto.getOtherUnitId())
            .orElseThrow(() -> new UnitNotFoundException("Unit ID: " + materialDto.getOtherUnitId() + " is not found!"));

            material.setOtherUnit(otherUnit);;
        }  

        Material savedMaterial = materialRepo.save(material);

        String materialBarcode = BarcodeGenUtil.generateBarcode(savedMaterial);
        savedMaterial.setMaterialBarcode(materialBarcode);

        Material newMaterial = materialRepo.save(savedMaterial);

        activityLogService.logActivity(
            "Material", 
            newMaterial.getMaterialId(),
            newMaterial.getMaterialName(),
            Action.CREATE, 
            "Created Material: " + newMaterial.getMaterialName());

            return newMaterial;
    }

    @Override
    public List<MaterialTableViewDto> viewMaterialTable() {
        
        List<Material> materials = materialRepo.findAllByOrderByMaterialIdDesc();

        List<MaterialTableViewDto> materialTableViewDtoList = materials.stream().map(material ->{
            
            MaterialTableViewDto materialTableViewDto = new MaterialTableViewDto();

            materialTableViewDto.setMaterialId(material.getMaterialId());
            materialTableViewDto.setMaterialName(material.getMaterialName());
            materialTableViewDto.setMaterialDescription(material.getMaterialDescription());
            materialTableViewDto.setMaterialSKU(material.getMaterialSKU());
            materialTableViewDto.setMaterialPurchasePrice(material.getMaterialPurchasePrice());
            materialTableViewDto.setQuantityInHand(material.getQuantityInHand());
            materialTableViewDto.setMaterialStatus(material.getMaterialStatus());

            if (material.getMaterialImage() != null) {
                String imageUrl = ImageUrlUtil.constructMaterialImageUrl(material.getMaterialId());
                materialTableViewDto.setMaterialImageUrl(imageUrl);
            }

            return materialTableViewDto;
        }).collect(Collectors.toList());

        return materialTableViewDtoList;
    }

    @Override
    public List<MaterialSideDropViewDto> viewMaterialSideDrop() {
        List<Material> materials = materialRepo.findAllByOrderByMaterialIdDesc();

        List<MaterialSideDropViewDto> materialSideDropViewDtoList = materials.stream().map(material ->{
            
            MaterialSideDropViewDto materialSideDropViewDto = new MaterialSideDropViewDto();

            materialSideDropViewDto.setMaterialId(material.getMaterialId());
            materialSideDropViewDto.setMaterialName(material.getMaterialName());
            materialSideDropViewDto.setMaterialSKU(material.getMaterialSKU());

            if (material.getMaterialImage() != null) {
                String imageUrl = ImageUrlUtil.constructMaterialImageUrl(material.getMaterialId());
                materialSideDropViewDto.setMaterialImageUrl(imageUrl);
            }

            return materialSideDropViewDto;
        }).collect(Collectors.toList());

        return materialSideDropViewDtoList;
    }

    @Override
    public MaterialViewDto viewMaterialById(Long materialId) {
        
        Material material = materialRepo.findById(materialId)
        .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialId + " is not found!"));

        MaterialViewDto materialViewDto = new MaterialViewDto();

        materialViewDto.setMaterialId(material.getMaterialId());
        materialViewDto.setMaterialName(material.getMaterialName());
        materialViewDto.setMaterialSKU(material.getMaterialSKU());
        materialViewDto.setMaterialBarcode(material.getMaterialBarcode());
        materialViewDto.setMaterialDescription(material.getMaterialDescription());
        materialViewDto.setMaterialPartNumber(material.getMaterialPartNumber());
        materialViewDto.setMaterialInventoryType(material.getMaterialInventoryType());
        materialViewDto.setMaterialType(material.getMaterialType());
        materialViewDto.setBrandName(material.getBrand().getBrandName());
        materialViewDto.setCategoryName(material.getCategory().getCategoryName());
        materialViewDto.setSubCategoryName(material.getSubCategory().getCategoryName());
        materialViewDto.setMaterialPurchasePrice(material.getMaterialPurchasePrice());
        materialViewDto.setMaterialMarketPrice(material.getMaterialMarketPrice());
        materialViewDto.setAlertQuantity(material.getAlertQuantity());
        materialViewDto.setQuantityInHand(material.getQuantityInHand());
        materialViewDto.setBaseUnitName(material.getBaseUnit().getUnitName());
        materialViewDto.setOtherUnitName(material.getOtherUnit().getUnitName());
        materialViewDto.setMaterialStatus(material.getMaterialStatus());
        materialViewDto.setMaterialForUse(material.getMaterialForUse());

        if (material.getMaterialImage() != null) {
                String imageUrl = ImageUrlUtil.constructMaterialImageUrl(material.getMaterialId());
                materialViewDto.setMaterialImageUrl(imageUrl);
            }

        return materialViewDto;

    }

    @Override
    public ResponseEntity<byte[]> viewMaterialImage(Long materialId) {
        return materialRepo.findById(materialId)
            .filter(material -> material.getMaterialImage() != null)
            .map(material -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(material.getMaterialImage()))
            .orElse(ResponseEntity.notFound().build());

    }

    @Override
    public Material updateMaterial(Long materialId, MaterialDto materialDto) {
        
        Material material = materialRepo.findById(materialId)
        .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialId + " is not found!"));

        if (!material.getMaterialName().equalsIgnoreCase(materialDto.getMaterialName())) {
            Optional<Material> existingMaterialName = materialRepo.findByMaterialName(materialDto.getMaterialName());
            if (existingMaterialName.isPresent()) {
                throw new DataIntegrityViolationException("A Material with the name " + materialDto.getMaterialName() + " already exists!");
            }
        }

        if (!material.getMaterialSKU().equalsIgnoreCase(materialDto.getMaterialSKU())) {
            Optional<Material> existingMaterialSKU = materialRepo.findByMaterialSKU(materialDto.getMaterialSKU());
            if (existingMaterialSKU.isPresent()) {
                throw new DataIntegrityViolationException("A Material with the SKU " + materialDto.getMaterialSKU() + " already exists!");
            }
        }

        MaterialUpdateDto existingMaterial = MaterialUpdateDto.builder()
        .materialId(material.getMaterialId())
        .materialName(material.getMaterialName())
        .materialDescription(material.getMaterialDescription())
        .materialPartNumber(material.getMaterialPartNumber())
        .materialSKU(material.getMaterialSKU())
        .materialImage(material.getMaterialImage() != null ? material.getMaterialImage().clone() : null)
        .materialMarketPrice(material.getMaterialMarketPrice())
        .materialPurchasePrice(material.getMaterialPurchasePrice())
        .alertQuantity(material.getAlertQuantity())
        .materialForUse(material.getMaterialForUse())
        .materialType(material.getMaterialType())
        .materialInventoryType(material.getMaterialInventoryType())
        .brandName(material.getBrand().getBrandName())
        .categoryName(material.getCategory().getCategoryName())
        .subCategoryName(material.getSubCategory().getCategoryName())
        .baseUnitName(material.getBaseUnit().getUnitName())
        .otherUnitName(material.getOtherUnit().getUnitName())
        .build();
        

        material.setMaterialName(materialDto.getMaterialName());
        material.setMaterialDescription(materialDto.getMaterialDescription());
        material.setMaterialPartNumber(materialDto.getMaterialPartNumber());
        material.setMaterialSKU(materialDto.getMaterialSKU());

        try{
            if(materialDto.getMaterialImage() != null && !materialDto.getMaterialImage().isEmpty()){
                material.setMaterialImage(materialDto.getMaterialImage().getBytes());
            }
            else if (materialDto.getMaterialImage() == null && materialDto.getMaterialImage().isEmpty()){
                material.setMaterialImage(null);
            }
        }
        catch(IOException e){
            throw new IllegalArgumentException("Failed to process image file!");
        }

        material.setMaterialMarketPrice(materialDto.getMaterialMarketPrice());
        material.setMaterialPurchasePrice(materialDto.getMaterialPurchasePrice());
        material.setAlertQuantity(materialDto.getAlertQuantity());
        material.setMaterialForUse(materialDto.getMaterialForUse());
        material.setMaterialType(materialDto.getMaterialType());
        material.setMaterialInventoryType(materialDto.getMaterialInventoryType());

        if (materialDto.getBrandId() != null){
            Brand brand = brandRepo.findById(materialDto.getBrandId())
            .orElseThrow(() -> new BrandNotFoundException("Brand ID: " + materialDto.getBrandId() + " is not found!"));

            material.setBrand(brand);
        }

        if (materialDto.getCategoryId() != null){
            Category category = categoryRepo.findById(materialDto.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("Category ID: " + materialDto.getCategoryId() + " is not found!"));

            material.setCategory(category);
        }

        if (materialDto.getSubCategoryId() != null){
            Category subCategory = categoryRepo.findById(materialDto.getSubCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("Sub - category ID: " + materialDto.getSubCategoryId() + " is not found!"));

            material.setSubCategory(subCategory);
        }        

        if (materialDto.getBaseUnitId() != null){
            Unit baseUnit = unitRepo.findById(materialDto.getBaseUnitId())
            .orElseThrow(() -> new UnitNotFoundException("Unit ID: " + materialDto.getBaseUnitId() + " is not found!"));

            material.setBaseUnit(baseUnit);
        }  

        if (materialDto.getOtherUnitId() != null){
            Unit otherUnit = unitRepo.findById(materialDto.getOtherUnitId())
            .orElseThrow(() -> new UnitNotFoundException("Unit ID: " + materialDto.getOtherUnitId() + " is not found!"));

            material.setOtherUnit(otherUnit);;
        }

        Material updatedMaterial = materialRepo.save(material);

        MaterialUpdateDto newMaterial = MaterialUpdateDto.builder()
        .materialId(updatedMaterial.getMaterialId())
        .materialName(updatedMaterial.getMaterialName())
        .materialDescription(updatedMaterial.getMaterialDescription())
        .materialPartNumber(updatedMaterial.getMaterialPartNumber())
        .materialSKU(updatedMaterial.getMaterialSKU())
        .materialImage(updatedMaterial.getMaterialImage() != null ? material.getMaterialImage().clone() : null)
        .materialMarketPrice(updatedMaterial.getMaterialMarketPrice())
        .materialPurchasePrice(updatedMaterial.getMaterialPurchasePrice())
        .alertQuantity(updatedMaterial.getAlertQuantity())
        .materialForUse(updatedMaterial.getMaterialForUse())
        .materialType(updatedMaterial.getMaterialType())
        .materialInventoryType(updatedMaterial.getMaterialInventoryType())
        .brandName(updatedMaterial.getBrand().getBrandName())
        .categoryName(updatedMaterial.getCategory().getCategoryName())
        .subCategoryName(updatedMaterial.getSubCategory().getCategoryName())
        .baseUnitName(updatedMaterial.getBaseUnit().getUnitName())
        .otherUnitName(updatedMaterial.getOtherUnit().getUnitName())
        .build();

        String changes = EntityDiffUtil.describeChanges(existingMaterial, newMaterial);

        activityLogService.logActivity(
            "Material", 
            newMaterial.getMaterialId(),
            newMaterial.getMaterialName(),
            Action.UPDATE, 
            changes.isBlank() ? "No changes detected" : changes);

            return updatedMaterial;
    }

    @Override
    public void deleteMaterial(Long materialId) {
        
        Material material = materialRepo.findById(materialId)
        .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialId + " is not found!"));

        material.setMaterialStatus(Status.INACTIVE);

        materialRepo.save(material);

        activityLogService.logActivity(
            "Material", 
            material.getMaterialId(),
            material.getMaterialName(),
            Action.DELETE, 
            "Deleted Material: " + material.getMaterialName());
    }

}