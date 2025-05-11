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

import com.morphgen.synexis.dto.BrandDto;
import com.morphgen.synexis.dto.BrandSideDropViewDto;
import com.morphgen.synexis.dto.BrandTableViewDto;
import com.morphgen.synexis.dto.BrandViewDto;
import com.morphgen.synexis.entity.Brand;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.Status;
import com.morphgen.synexis.exception.BrandNotFoundException;
import com.morphgen.synexis.repository.BrandRepo;
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

    @Override
    public Brand createBrand(BrandDto brandDto) {
        
        Optional<Brand> existingBrand = brandRepo.findByBrandName(brandDto.getBrandName());
        if(existingBrand.isPresent()){
            throw new DataIntegrityViolationException("A Brand with the name " + brandDto.getBrandName() + " already exists!");
        }

        Brand brand = new Brand();

        brand.setBrandName(brandDto.getBrandName());
        brand.setBrandDescription(brandDto.getBrandDescription());
        brand.setBrandCountry(brandDto.getBrandCountry());
        brand.setBrandWebsite(brandDto.getBrandWebsite());

        try{
            if(brandDto.getBrandImage() !=null && !brandDto.getBrandImage().isEmpty()){
                brand.setBrandImage(brandDto.getBrandImage().getBytes());
            }
        }
        catch(IOException e) {
            throw new IllegalArgumentException("Failed to process image file!");
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
            .filter(brand -> brand.getBrandImage() != null)
            .map(brand -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(brand.getBrandImage()))
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

            if (brand.getBrandImage() != null) {
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

            if (brand.getBrandImage() != null) {
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

        BrandViewDto brandViewDto = new BrandViewDto();

        brandViewDto.setBrandId(brandId);
        brandViewDto.setBrandName(brand.getBrandName());
        brandViewDto.setBrandDescription(brand.getBrandDescription());
        brandViewDto.setBrandCountry(brand.getBrandCountry());
        brandViewDto.setBrandWebsite(brand.getBrandWebsite());
        brandViewDto.setBrandStatus(brand.getBrandStatus());

        if (brand.getBrandImage() != null) {
            String imageUrl = ImageUrlUtil.constructImageUrl(brand.getBrandId());
            brandViewDto.setBrandImageUrl(imageUrl);
        }

        return brandViewDto;

    }

    @Override
    public Brand updateBrand(Long brandId, BrandDto brandDto) {
        
        Brand brand = brandRepo.findById(brandId)
        .orElseThrow(() -> new BrandNotFoundException("Brand ID: " + brandId + " is not found!"));

        Optional<Brand> oldBrand = brandRepo.findByBrandName(brandDto.getBrandName());
        if(oldBrand.isPresent()){
            throw new DataIntegrityViolationException("A Brand with the name " + brandDto.getBrandName() + " already exists!");
        }

        Brand existingBrand = Brand.builder()
        .brandId(brand.getBrandId())
        .brandName(brand.getBrandName())
        .brandDescription(brand.getBrandDescription())
        .brandCountry(brand.getBrandCountry())
        .brandWebsite(brand.getBrandWebsite())
        .brandImage(brand.getBrandImage() != null ? brand.getBrandImage().clone() : null)
        .brandStatus(brand.getBrandStatus())
        .build();

        brand.setBrandName(brandDto.getBrandName());
        brand.setBrandDescription(brandDto.getBrandDescription());
        brand.setBrandCountry(brandDto.getBrandCountry());
        brand.setBrandWebsite(brandDto.getBrandWebsite());

        try{
            if(brandDto.getBrandImage() !=null && !brandDto.getBrandImage().isEmpty()){
                brand.setBrandImage(brandDto.getBrandImage().getBytes());
            }
            else if(brandDto.getBrandImage() == null || brandDto.getBrandImage().isEmpty()){
                brand.setBrandImage(null);
            }
        }
        catch(IOException e) {
            throw new RuntimeException("Failed to process image file", e);
        }

        Brand updatedBrand = brandRepo.save(brand);

        String changes = EntityDiffUtil.describeChanges(existingBrand, updatedBrand);

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

        activityLogService.logActivity("Brand", brandId, brand.getBrandName(), Action.DELETE, "Deleted Brand: " + brand.getBrandName());
    }

}
