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
import org.springframework.util.StringUtils;

import com.morphgen.synexis.dto.BrandDropDownDto;
import com.morphgen.synexis.dto.BrandDto;
import com.morphgen.synexis.dto.BrandSideDropViewDto;
import com.morphgen.synexis.dto.BrandTableViewDto;
import com.morphgen.synexis.dto.BrandUpdateDto;
import com.morphgen.synexis.dto.BrandViewDto;
import com.morphgen.synexis.dto.MaterialTableViewDto;
import com.morphgen.synexis.entity.Brand;
import com.morphgen.synexis.entity.BrandImage;
import com.morphgen.synexis.entity.Material;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.BrandNotFoundException;
import com.morphgen.synexis.exception.ImageProcessingException;
import com.morphgen.synexis.exception.InvalidInputException;
import com.morphgen.synexis.repository.BrandRepo;
import com.morphgen.synexis.repository.MaterialRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.BrandService;
import com.morphgen.synexis.utils.EntityDiffUtil;
import com.morphgen.synexis.utils.ImageUrlUtil;

@Service

public class BrandServiceImpl implements BrandService {
    
    @Autowired
    private BrandRepo brandRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private MaterialRepo materialRepo;

    @Override
    @Transactional
    public Brand createBrand(BrandDto brandDto) {
        
        if(brandDto.getBrandName() == null || brandDto.getBrandName().isEmpty()){
            throw new InvalidInputException("Brand name cannot be empty!");
        }
        else if(brandDto.getBrandCountry() == null || brandDto.getBrandCountry().isEmpty()){
            throw new InvalidInputException("Brand country cannot be null empty!");
        }

        Optional<Brand> existingBrand = brandRepo.findByBrandName(brandDto.getBrandName());
        if(existingBrand.isPresent()){

            Brand activeBrand = existingBrand.get();

            if (activeBrand.getBrandStatus() == Status.ACTIVE){

                throw new DataIntegrityViolationException("A Brand with the name " + brandDto.getBrandName() + " already exists!");
            }
            else {

                throw new DataIntegrityViolationException("A Brand with the name " + brandDto.getBrandName() + " already exists but is currently inactive. Consider reactivating it.");
            }
        }

        if (brandDto.getBrandWebsite() != null){
            Optional<Brand> existingBrandWebsite = brandRepo.findByBrandWebsite(brandDto.getBrandWebsite());
            if(existingBrandWebsite.isPresent()){
                throw new DataIntegrityViolationException("A Brand with the website " + brandDto.getBrandWebsite() + " already exists!");
            }
        }

        Brand brand = new Brand();

        brand.setBrandName(brandDto.getBrandName());
        brand.setBrandDescription(brandDto.getBrandDescription());
        brand.setBrandCountry(brandDto.getBrandCountry());
        brand.setBrandWebsite(brandDto.getBrandWebsite());
        
        try{
            if(brandDto.getBrandImage() !=null && !brandDto.getBrandImage().isEmpty()){
                BrandImage brandImage = new BrandImage();
                
                brandImage.setBrandImageName(brandDto.getBrandImage().getOriginalFilename());
                brandImage.setBrandImageType(brandDto.getBrandImage().getContentType());
                brandImage.setBrandImageSize(brandDto.getBrandImage().getSize());
                brandImage.setBrandImageData(brandDto.getBrandImage().getBytes());
                brandImage.setBrand(brand);

                brand.setBrandImage(brandImage);
            }
        }
        catch(IOException e) {
            throw new ImageProcessingException("Unable to process image. Please ensure the image is valid and try again!");
        }

        Brand newBrand = brandRepo.save(brand);

        activityLogService.logActivity(
            "Brand", 
            newBrand.getBrandId(),
            newBrand.getBrandName(),
            Action.CREATE, 
            "Created Brand: " + newBrand.getBrandName());

        return newBrand;
    }

    @Override
    public ResponseEntity<byte[]> viewBrandImage(Long brandId) {
        return brandRepo.findById(brandId)
            .filter(brand -> brand.getBrandImage().getBrandImageData() != null)
            .map(brand -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(brand.getBrandImage().getBrandImageData()))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public List<BrandTableViewDto> viewBrandTable() {
        
        List<Brand> brands = brandRepo.findAllByOrderByBrandIdDesc();

        List<BrandTableViewDto> brandTableViewDtoList = brands.stream().map(brand ->{

            BrandTableViewDto brandTableViewDto = new BrandTableViewDto();

            brandTableViewDto.setBrandId(brand.getBrandId());
            brandTableViewDto.setBrandName(brand.getBrandName());
            brandTableViewDto.setBrandDescription(brand.getBrandDescription());
            brandTableViewDto.setBrandCountry(brand.getBrandCountry());
            brandTableViewDto.setBrandStatus(brand.getBrandStatus());

            if (brand.getBrandImage().getBrandImageData() != null) {
                String imageUrl = ImageUrlUtil.constructImageUrl(brand.getBrandId());
                brandTableViewDto.setBrandImageUrl(imageUrl);
            }

            return brandTableViewDto;
        }).collect(Collectors.toList());

        return brandTableViewDtoList;
    }

    @Override
    public List<BrandSideDropViewDto> viewBrandSideDrop() {
        
        List<Brand> brands = brandRepo.findAllByOrderByBrandIdDesc();

        List<BrandSideDropViewDto> brandSideDropViewDtoList = brands.stream().map(brand ->{

            BrandSideDropViewDto brandSideDropViewDto = new BrandSideDropViewDto();

            brandSideDropViewDto.setBrandId(brand.getBrandId());
            brandSideDropViewDto.setBrandName(brand.getBrandName());

            if (brand.getBrandImage().getBrandImageData() != null) {
                String imageUrl = ImageUrlUtil.constructImageUrl(brand.getBrandId());
                brandSideDropViewDto.setBrandImageUrl(imageUrl);
            }

            return brandSideDropViewDto;
        }).collect(Collectors.toList());

        return brandSideDropViewDtoList;
    }

    @Override
    public BrandViewDto viewBrandById(Long brandId) {
        
        Brand brand = brandRepo.findById(brandId)
        .orElseThrow(() -> new BrandNotFoundException("Brand ID: " + brandId + " is not found!"));

        List<Material> materials = materialRepo.findMaterialsByBrandId(brandId);

        BrandViewDto brandViewDto = new BrandViewDto();

        brandViewDto.setBrandId(brandId);
        brandViewDto.setBrandName(brand.getBrandName());
        brandViewDto.setBrandDescription(brand.getBrandDescription());
        brandViewDto.setBrandCountry(brand.getBrandCountry());
        brandViewDto.setBrandWebsite(brand.getBrandWebsite());
        brandViewDto.setBrandStatus(brand.getBrandStatus());

        if (brand.getBrandImage().getBrandImageData() != null) {
            String imageUrl = ImageUrlUtil.constructImageUrl(brand.getBrandId());
            brandViewDto.setBrandImageUrl(imageUrl);
        }

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

        brandViewDto.setMaterialTableViewDtoList(materialTableViewDtoList);

        return brandViewDto;

    }

    @Override
    public Brand updateBrand(Long brandId, BrandDto brandDto) {
        
        if(brandDto.getBrandName() == null || brandDto.getBrandName().isEmpty()){
            throw new InvalidInputException("Brand name cannot be empty!");
        }
        else if(brandDto.getBrandCountry() == null || brandDto.getBrandCountry().isEmpty()){
            throw new InvalidInputException("Brand country cannot be null empty!");
        }

        Brand brand = brandRepo.findById(brandId)
        .orElseThrow(() -> new BrandNotFoundException("Brand ID: " + brandId + " is not found!"));

        if(!brand.getBrandName().equalsIgnoreCase(brandDto.getBrandName())){
            Optional<Brand> oldBrand = brandRepo.findByBrandName(brandDto.getBrandName());
            if(oldBrand.isPresent()){
                throw new DataIntegrityViolationException("A Brand with the name " + brandDto.getBrandName() + " already exists!");
            }
        }

        if (StringUtils.hasText(brandDto.getBrandWebsite()) && !brandDto.getBrandWebsite().equalsIgnoreCase(brand.getBrandWebsite())){
            Optional<Brand> existingBrandWebsite = brandRepo.findByBrandWebsite(brandDto.getBrandWebsite());
            if(existingBrandWebsite.isPresent()){
                throw new DataIntegrityViolationException("A Brand with the website " + brandDto.getBrandWebsite() + " already exists!");
            }
        }

        BrandUpdateDto existingBrand = BrandUpdateDto.builder()
        .brandId(brand.getBrandId())
        .brandName(brand.getBrandName())
        .brandDescription(brand.getBrandDescription())
        .brandCountry(brand.getBrandCountry())
        .brandWebsite(brand.getBrandWebsite())
        .brandImage(brand.getBrandImage() != null && brand.getBrandImage().getBrandImageData() != null ? brand.getBrandImage().getBrandImageData().clone() : null)
        .brandStatus(brand.getBrandStatus())
        .build();

        brand.setBrandName(brandDto.getBrandName());
        brand.setBrandDescription(brandDto.getBrandDescription());
        brand.setBrandCountry(brandDto.getBrandCountry());
        brand.setBrandWebsite(brandDto.getBrandWebsite());

        try{
            if(brandDto.getBrandImage() !=null && !brandDto.getBrandImage().isEmpty()){
                BrandImage brandImage = brand.getBrandImage();
                if (brandImage == null) {
                    brandImage = new BrandImage();
                }
                
                brandImage.setBrandImageName(brandDto.getBrandImage().getOriginalFilename());
                brandImage.setBrandImageType(brandDto.getBrandImage().getContentType());
                brandImage.setBrandImageSize(brandDto.getBrandImage().getSize());
                brandImage.setBrandImageData(brandDto.getBrandImage().getBytes());
                brandImage.setBrand(brand);

                brand.setBrandImage(brandImage);
            }
            else if (brand.getBrandImage() != null) {
                BrandImage brandImage = brand.getBrandImage();
                brandImage.setBrandImageName(null);
                brandImage.setBrandImageType(null);
                brandImage.setBrandImageSize(null);
                brandImage.setBrandImageData(null);
                brandImage.setBrand(brand);
                brand.setBrandImage(null);
            }
        }
        catch(IOException e) {
            throw new ImageProcessingException("Unable to process image. Please ensure the image is valid and try again!");
        }

        Brand updatedBrand = brandRepo.save(brand);

        BrandUpdateDto newBrand = BrandUpdateDto.builder()
        .brandId(updatedBrand.getBrandId())
        .brandName(updatedBrand.getBrandName())
        .brandDescription(updatedBrand.getBrandDescription())
        .brandCountry(updatedBrand.getBrandCountry())
        .brandWebsite(updatedBrand.getBrandWebsite())
        .brandImage(updatedBrand.getBrandImage() != null && updatedBrand.getBrandImage().getBrandImageData() != null ? updatedBrand.getBrandImage().getBrandImageData().clone() : null)
        .brandStatus(updatedBrand.getBrandStatus())
        .build();

        String changes = EntityDiffUtil.describeChanges(existingBrand, newBrand);

        activityLogService.logActivity(
            "Brand", 
            updatedBrand.getBrandId(),
            updatedBrand.getBrandName(), 
            Action.UPDATE, 
            changes.isBlank() ? "No changes detected" : changes);

        return updatedBrand;

    }

    @Override
    public void deleteBrand(Long brandId) {
        
        Brand brand = brandRepo.findById(brandId)
        .orElseThrow(() -> new BrandNotFoundException("Brand ID: " + brandId + " is not found!"));

        brand.setBrandStatus(Status.INACTIVE);

        brandRepo.save(brand);

        activityLogService.logActivity(
            "Brand", 
            brandId, 
            brand.getBrandName(), 
            Action.DELETE, 
            "Deleted Brand: " + brand.getBrandName());
    }

    @Override
    public List<BrandDropDownDto> brandDropDown(String searchBrand) {
        
        List<Brand> brands = brandRepo.searchActiveBrands(searchBrand);

        List<BrandDropDownDto> brandDropDownDtoList = brands.stream().map(brand ->{

            BrandDropDownDto brandDropDownDto = new BrandDropDownDto();

            brandDropDownDto.setBrandId(brand.getBrandId());
            brandDropDownDto.setBrandName(brand.getBrandName());

            return brandDropDownDto;
        }).collect(Collectors.toList());

        return brandDropDownDtoList;
    }

}
