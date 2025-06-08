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

import com.morphgen.synexis.dto.MaterialDropDownDto;
import com.morphgen.synexis.dto.MaterialDto;
import com.morphgen.synexis.dto.MaterialSideDropViewDto;
import com.morphgen.synexis.dto.MaterialTableViewDto;
import com.morphgen.synexis.dto.MaterialUpdateDto;
import com.morphgen.synexis.dto.MaterialViewDto;
import com.morphgen.synexis.entity.Brand;
import com.morphgen.synexis.entity.Category;
import com.morphgen.synexis.entity.Material;
import com.morphgen.synexis.entity.MaterialImage;
import com.morphgen.synexis.entity.Unit;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.BrandNotFoundException;
import com.morphgen.synexis.exception.CategoryNotFoundException;
import com.morphgen.synexis.exception.ImageNotFoundException;
import com.morphgen.synexis.exception.ImageProcessingException;
import com.morphgen.synexis.exception.InvalidInputException;
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

        if (materialDto.getMaterialName() == null || materialDto.getMaterialName().isEmpty()){
            throw new InvalidInputException("Material name cannot be empty!");
        }
        else if (materialDto.getMaterialSKU() == null || materialDto.getMaterialSKU().isEmpty()){
            throw new InvalidInputException("Stock Keeping unit cannot be empty!");
        }
        else if (materialDto.getAlertQuantity() == null){
            throw new InvalidInputException("Alert quantity cannot be empty!");
        }
        else if (materialDto.getMaterialType() == null){
            throw new InvalidInputException("Material type cannot be empty!");
        }
        else if (materialDto.getMaterialInventoryType() == null){
            throw new InvalidInputException("Inventory Type cannot be empty!");
        }
        
        Optional<Material> existingMaterialName = materialRepo.findByMaterialName(materialDto.getMaterialName());
        if (existingMaterialName.isPresent()) {

            Material activeMaterialN = existingMaterialName.get();
            
            if (activeMaterialN.getMaterialStatus() == Status.ACTIVE){

                throw new DataIntegrityViolationException("A Material with the name " + materialDto.getMaterialName() + " already exists!");
            }
            else {

                throw new DataIntegrityViolationException("A Material with the name " + materialDto.getMaterialName() + " already exists but is currently inactive. Consider reactivating it.");
            }
        }
        Optional<Material> existingMaterialSKU = materialRepo.findByMaterialSKU(materialDto.getMaterialSKU());
        if (existingMaterialSKU.isPresent()) {
            
            Material activeMaterialS = existingMaterialSKU.get();
            
            if (activeMaterialS.getMaterialStatus() == Status.ACTIVE){

                throw new DataIntegrityViolationException("A Material with the SKU " + materialDto.getMaterialSKU() + " already exists!");
            }
            else {

                throw new DataIntegrityViolationException("A Material with the SKU " + materialDto.getMaterialSKU() + " already exists but is currently inactive. Consider reactivating it.");
            }
        }

        Material material = new Material();

        material.setMaterialName(materialDto.getMaterialName());
        material.setMaterialDescription(materialDto.getMaterialDescription());
        material.setMaterialPartNumber(materialDto.getMaterialPartNumber());
        material.setMaterialMake(materialDto.getMaterialMake());
        material.setMaterialSKU(materialDto.getMaterialSKU());

        try{
            if(materialDto.getMaterialImage() != null && !materialDto.getMaterialImage().isEmpty()){
                
                MaterialImage materialImage = new MaterialImage();

                materialImage.setMaterialImageName(materialDto.getMaterialImage().getOriginalFilename());
                materialImage.setMaterialImageType(materialDto.getMaterialImage().getContentType());
                materialImage.setMaterialImageSize(materialDto.getMaterialImage().getSize());
                materialImage.setMaterialImageData(materialDto.getMaterialImage().getBytes());
                materialImage.setMaterial(material);

                material.setMaterialImage(materialImage);
            }
        }
        catch(IOException e){
            throw new ImageProcessingException("Unable to process image. Please ensure the image is valid and try again!");
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

            if (material.getMaterialImage().getMaterialImageData() != null) {
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

            if (material.getMaterialImage().getMaterialImageData() != null) {
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
        materialViewDto.setMaterialMake(material.getMaterialMake());
        materialViewDto.setMaterialInventoryType(material.getMaterialInventoryType());
        materialViewDto.setMaterialType(material.getMaterialType());
        materialViewDto.setBrandName(material.getBrand().getBrandName());
        materialViewDto.setBrandId(material.getBrand().getBrandId());
        materialViewDto.setCategoryName(material.getCategory().getCategoryName());
        materialViewDto.setCategoryId(material.getCategory().getCategoryId());
        materialViewDto.setSubCategoryName(material.getSubCategory().getCategoryName());
        materialViewDto.setSubCategoryId(material.getSubCategory().getCategoryId());
        materialViewDto.setMaterialPurchasePrice(material.getMaterialPurchasePrice());
        materialViewDto.setMaterialMarketPrice(material.getMaterialMarketPrice());
        materialViewDto.setAlertQuantity(material.getAlertQuantity());
        materialViewDto.setQuantityInHand(material.getQuantityInHand());
        materialViewDto.setBaseUnitName(material.getBaseUnit().getUnitName());
        materialViewDto.setBaseUnitId(material.getBaseUnit().getUnitId());
        materialViewDto.setOtherUnitName(material.getOtherUnit().getUnitName());
        materialViewDto.setOtherUnitId(material.getOtherUnit().getUnitId());
        materialViewDto.setMaterialStatus(material.getMaterialStatus());
        materialViewDto.setMaterialForUse(material.getMaterialForUse());

        if (material.getMaterialImage().getMaterialImageData() != null) {
                String imageUrl = ImageUrlUtil.constructMaterialImageUrl(material.getMaterialId());
                materialViewDto.setMaterialImageUrl(imageUrl);
            }

        return materialViewDto;

    }

    @Override
    public ResponseEntity<byte[]> viewMaterialImage(Long materialId) {
        return materialRepo.findById(materialId)
            .filter(material -> material.getMaterialImage().getMaterialImageData() != null)
            .map(material -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(material.getMaterialImage().getMaterialImageData()))
            .orElseThrow(() -> new ImageNotFoundException("Brand image for " + materialId  + "is not found or has no image data!"));
    }

    @Override
    @Transactional
    public Material updateMaterial(Long materialId, MaterialDto materialDto) {
        
        Material material = materialRepo.findById(materialId)
        .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialId + " is not found!"));

        if (materialDto.getMaterialName() == null || materialDto.getMaterialName().isEmpty()){
            throw new InvalidInputException("Material name cannot be empty!");
        }
        else if (materialDto.getMaterialSKU() == null || materialDto.getMaterialSKU().isEmpty()){
            throw new InvalidInputException("Stock Keeping unit cannot be empty!");
        }
        else if (materialDto.getAlertQuantity() == null){
            throw new InvalidInputException("Alert quantity cannot be empty!");
        }
        else if (materialDto.getMaterialType() == null){
            throw new InvalidInputException("Material type cannot be empty!");
        }
        else if (materialDto.getMaterialInventoryType() == null){
            throw new InvalidInputException("Inventory Type cannot be empty!");
        }

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
        .materialMake(material.getMaterialMake())
        .materialSKU(material.getMaterialSKU())
        .materialImage(material.getMaterialImage().getMaterialImageData() != null ? material.getMaterialImage().getMaterialImageData().clone() : null)
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
        material.setMaterialMake(materialDto.getMaterialMake());
        material.setMaterialSKU(materialDto.getMaterialSKU());

        try{
            if (materialDto.getMaterialImage() != null && !materialDto.getMaterialImage().isEmpty()){
                
                MaterialImage materialImage = new MaterialImage();

                materialImage.setMaterialImageName(materialDto.getMaterialImage().getOriginalFilename());
                materialImage.setMaterialImageType(materialDto.getMaterialImage().getContentType());
                materialImage.setMaterialImageSize(materialDto.getMaterialImage().getSize());
                materialImage.setMaterialImageData(materialDto.getMaterialImage().getBytes());
                materialImage.setMaterial(material);

                material.setMaterialImage(materialImage);
            }
            else if (material.getMaterialImage() != null){

                MaterialImage materialImage = new MaterialImage();

                materialImage.setMaterialImageName(null);
                materialImage.setMaterialImageType(null);
                materialImage.setMaterialImageSize(null);
                materialImage.setMaterialImageData(null);
                materialImage.setMaterial(material);

                material.setMaterialImage(null);
            }
        }
        catch(IOException e){
            throw new ImageProcessingException("Unable to process image. Please ensure the image is valid and try again!");
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
        .materialMake(updatedMaterial.getMaterialMake())
        .materialSKU(updatedMaterial.getMaterialSKU())
        .materialImage(updatedMaterial.getMaterialImage().getMaterialImageData() != null ? material.getMaterialImage().getMaterialImageData().clone() : null)
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

    @Override
    public List<MaterialDropDownDto> viewMaterialDropDown(String searchMaterial) {
        
        List<Material> materials = materialRepo.searchActiveMaterials(searchMaterial.trim());

        List<MaterialDropDownDto> materialDropDownDtoList = materials.stream().map(material ->{
            
            MaterialDropDownDto materialDropDownDto = new MaterialDropDownDto();

            materialDropDownDto.setMaterialId(material.getMaterialId());
            materialDropDownDto.setMaterialName(material.getMaterialName());
            materialDropDownDto.setMaterialDescription(material.getMaterialDescription());
            materialDropDownDto.setMaterialMarketPrice(material.getMaterialMarketPrice());
            materialDropDownDto.setMaterialPartNumber(material.getMaterialPartNumber());
            materialDropDownDto.setMaterialType(material.getMaterialType());
            materialDropDownDto.setMaterialMake(material.getMaterialMake());
            materialDropDownDto.setMaterialCountry(material.getBrand().getBrandCountry());

            return materialDropDownDto;
        }).collect(Collectors.toList());

        return materialDropDownDtoList;
    }

    @Override
    public void reactivateMaterial(Long materialId) {
        
        Material material = materialRepo.findById(materialId)
        .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialId + " is not found!"));

        if (material.getMaterialStatus() == Status.ACTIVE){
            throw new DataIntegrityViolationException("Material is already active!");
        }

        material.setMaterialStatus(Status.ACTIVE);

        materialRepo.save(material);

        activityLogService.logActivity(
            "Material", 
            material.getMaterialId(),
            material.getMaterialName(),
            Action.REACTIVATE, 
            "Reactivated Material: " + material.getMaterialName());
    }

}