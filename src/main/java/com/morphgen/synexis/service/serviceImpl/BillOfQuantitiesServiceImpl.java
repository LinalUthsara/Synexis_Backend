package com.morphgen.synexis.service.serviceImpl;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.morphgen.synexis.dto.BoqDesignDto;
import com.morphgen.synexis.dto.BoqDto;
import com.morphgen.synexis.dto.BoqItemDto;
import com.morphgen.synexis.dto.BoqItemMaterialDto;
import com.morphgen.synexis.dto.BoqItemMaterialViewDto;
import com.morphgen.synexis.dto.BoqItemViewDto;
import com.morphgen.synexis.dto.BoqTableDto;
import com.morphgen.synexis.dto.BoqTableViewDto;
import com.morphgen.synexis.dto.BoqViewDto;
import com.morphgen.synexis.dto.CustomerDesignAssetViewDto;
import com.morphgen.synexis.dto.CustomerDesignDto;
import com.morphgen.synexis.dto.CustomerDesignViewDto;
import com.morphgen.synexis.entity.BillOfQuantities;
import com.morphgen.synexis.entity.BoqItem;
import com.morphgen.synexis.entity.BoqItemMaterial;
import com.morphgen.synexis.entity.CustomerDesign;
import com.morphgen.synexis.entity.Job;
import com.morphgen.synexis.entity.Material;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.BoqStatus;
import com.morphgen.synexis.enums.JobStatus;
import com.morphgen.synexis.exception.BoqNotFoundException;
import com.morphgen.synexis.exception.CustomerDesignNotFoundException;
import com.morphgen.synexis.exception.DesignProcessingException;
import com.morphgen.synexis.exception.IllegalStatusTransitionException;
import com.morphgen.synexis.exception.InvalidInputException;
import com.morphgen.synexis.exception.InvalidStatusException;
import com.morphgen.synexis.exception.JobNotFoundException;
import com.morphgen.synexis.exception.MaterialNotFoundException;
import com.morphgen.synexis.repository.*;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.BillOfQuantitiesService;
import com.morphgen.synexis.utils.CustomerDesignUrlUtil;
import com.morphgen.synexis.utils.MaterialTypeUtil;

@Service

public class BillOfQuantitiesServiceImpl implements BillOfQuantitiesService {

    @Autowired
    private CustomerDesignRepo customerDesignRepo;

    @Autowired
    private BillOfQuantitiesRepo billOfQuantitiesRepo;

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private MaterialRepo materialRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private BoqItemRepo boqItemRepo;

    @Autowired
    private BoqItemMaterialRepo boqItemMaterialRepo;

    @Override
    @Transactional
    public BillOfQuantities createBOQ(BoqDto boqDto) {

        if(boqDto.getJobId() == null){
            throw new InvalidInputException("Job Id cannot be empty!");
        }
        
        Job job = jobRepo.findById(boqDto.getJobId())
        .orElseThrow(() -> new JobNotFoundException("Job ID: " + boqDto.getJobId() + " is not found!"));

        if (job.getJobStatus() != JobStatus.APPROVED){
            throw new InvalidStatusException("Bill of Quantities can only be created for approved jobs!");
        }

        int currentCount = billOfQuantitiesRepo.countByJob_JobId(job.getJobId());

        String boqVersion = String.format("%03d", currentCount + 1);

        BillOfQuantities billOfQuantities = new BillOfQuantities();
        billOfQuantities.setJob(job);  
        billOfQuantities.setBoqVersion(boqVersion);
        billOfQuantities.setBoqStatus(boqDto.getBoqStatus());

        List<BoqItem> items = new ArrayList<>();

        for (BoqItemDto itemDto : boqDto.getItems()) {
            BoqItem item = new BoqItem();
            item.setItemName(itemDto.getItemName());
            item.setItemQuantity(itemDto.getItemQuantity());
            item.setBillOfQuantities(billOfQuantities);

            List<BoqItemMaterial> itemMaterials = new ArrayList<>();
            for (BoqItemMaterialDto itemMaterialDto : itemDto.getItemMaterials()) {

                Material material = materialRepo.findById(itemMaterialDto.getMaterialId())
                .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + itemMaterialDto.getMaterialId() + " is not found!"));

                BoqItemMaterial itemMaterial = new BoqItemMaterial();
                itemMaterial.setMaterial(material);
                itemMaterial.setMaterialQuantity(itemMaterialDto.getMaterialQuantity());
                itemMaterial.setItem(item);

                itemMaterials.add(itemMaterial);
            }

            item.setMaterials(itemMaterials);
            items.add(item);
        }

        billOfQuantities.setItems(items);

        BillOfQuantities newbillOfQuantities = billOfQuantitiesRepo.save(billOfQuantities);

        activityLogService.logActivity(
            "BillOfQuanitities", 
            newbillOfQuantities.getBoqId(),
            newbillOfQuantities.getBoqVersion(),
            Action.CREATE, 
            "Created Bill of Quantities Version: " + newbillOfQuantities.getBoqVersion() + " for Job: " + newbillOfQuantities.getJob().getEstimation().getInquiry().getProjectName());

        return newbillOfQuantities;
    }

    @Override
    public BoqTableViewDto viewBoqByJobId(Long jobId) {
        
        Job job = jobRepo.findById(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job ID: " + jobId + " is not found!"));

        List<BillOfQuantities> billOfQuantities = billOfQuantitiesRepo.findByJob_JobIdOrderByBoqIdDesc(job.getJobId());

        BoqTableViewDto boqTableViewDto = new BoqTableViewDto();

        boqTableViewDto.setJobId(job.getJobId());
        boqTableViewDto.setProjectName(job.getEstimation().getInquiry().getProjectName());
        boqTableViewDto.setQuotationVersion(job.getEstimation().getQuotationVersion());

        List<BoqTableDto> boqTableDtoList = billOfQuantities.stream().map(boq -> {

            BoqTableDto boqTableDto = new BoqTableDto();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

            boqTableDto.setBoqId(boq.getBoqId());
            boqTableDto.setBoqVersion(boq.getBoqVersion());
            boqTableDto.setBoqStatus(boq.getBoqStatus());
            boqTableDto.setLastModifiedDate(boq.getUpdatedAt().format(formatter));

            if (boq.getCustomerDesigns() != null && !boq.getCustomerDesigns().isEmpty()) {
                
                boqTableDto.setCustomerDesignPresent(true);
            } 
            else {
                
                boqTableDto.setCustomerDesignPresent(false);
            }

            return boqTableDto;

        }).collect(Collectors.toList());

        boqTableViewDto.setBoqs(boqTableDtoList);

        return boqTableViewDto;
    }

    @Override
    @Transactional
    public BillOfQuantities updateBOQ(Long boqId, BoqDto boqDto) {
        
        BillOfQuantities existingBoq = billOfQuantitiesRepo.findById(boqId)
        .orElseThrow(() -> new BoqNotFoundException("Cost Estimation ID: " + boqId + " is not found!"));

        existingBoq.setBoqStatus(boqDto.getBoqStatus());

        if (existingBoq.getItems() == null) {
            existingBoq.setItems(new ArrayList<>());
        }
    
        Map<Long, BoqItem> existingItemsMap = existingBoq.getItems().stream()
            .filter(item -> item.getItemId() != null)
            .collect(Collectors.toMap(BoqItem::getItemId, item -> item));

    
        Set<Long> updatedItemIds = new HashSet<>();
    
        for (BoqItemDto itemDto : boqDto.getItems()) {
            if (itemDto.getItemId() != null) {
                updatedItemIds.add(itemDto.getItemId());
            }
        }

        List<BoqItem> itemsToDelete = existingBoq.getItems().stream()
            .filter(item -> item.getItemId() != null && !updatedItemIds.contains(item.getItemId()))
            .collect(Collectors.toList());

        for (BoqItem itemToDelete : itemsToDelete) {

            if (itemToDelete.getMaterials() != null) {
                itemToDelete.getMaterials().clear();
            }

            existingBoq.getItems().remove(itemToDelete);

            boqItemRepo.delete(itemToDelete);
        }

        existingBoq.getItems().clear();
    

        for (BoqItemDto itemDto : boqDto.getItems()) {
            
            BoqItem item;
        
            if (itemDto.getItemId() != null && existingItemsMap.containsKey(itemDto.getItemId())) {

                item = existingItemsMap.get(itemDto.getItemId());
            

                item.setItemName(itemDto.getItemName());
                item.setItemQuantity(itemDto.getItemQuantity());
            } 
            else {
                
                item = new BoqItem();
                item.setItemName(itemDto.getItemName());
                item.setItemQuantity(itemDto.getItemQuantity());
                item.setBillOfQuantities(existingBoq);
            }


            updateBoqItemMaterials(item, itemDto.getItemMaterials());
        
            existingBoq.getItems().add(item);
        }

        BillOfQuantities updatedBoq = billOfQuantitiesRepo.save(existingBoq);

        activityLogService.logActivity(
        "BillOfQuanitities", 
        updatedBoq.getBoqId(),
        updatedBoq.getBoqVersion(),
        Action.UPDATE, 
        "Updated Bill of Quantities Version: " + updatedBoq.getBoqVersion());

        return updatedBoq;
    }

    private void updateBoqItemMaterials(BoqItem item, List<BoqItemMaterialDto> itemMaterialDtos) {
    
        if (item.getMaterials() == null) {
            item.setMaterials(new ArrayList<>());
        }

        Map<Long, BoqItemMaterial> existingMaterialsMap = item.getMaterials().stream()
            .filter(material -> material.getItemMaterialId() != null)
            .collect(Collectors.toMap(BoqItemMaterial::getItemMaterialId, material -> material));

        Set<Long> updatedMaterialIds = new HashSet<>();
    
        for (BoqItemMaterialDto materialDto : itemMaterialDtos) {
            if (materialDto.getItemMaterialId() != null) {
                updatedMaterialIds.add(materialDto.getItemMaterialId());
            }
        }

        List<BoqItemMaterial> materialsToDelete = item.getMaterials().stream()
            .filter(material -> material.getItemMaterialId() != null && !updatedMaterialIds.contains(material.getItemMaterialId()))
            .collect(Collectors.toList());

        for (BoqItemMaterial materialToDelete : materialsToDelete) {
            
            item.getMaterials().remove(materialToDelete);

            boqItemMaterialRepo.delete(materialToDelete);
        }

        item.getMaterials().clear();

        for (BoqItemMaterialDto materialDto : itemMaterialDtos) {
            BoqItemMaterial itemMaterial;
        
            if (materialDto.getItemMaterialId() != null && 
                existingMaterialsMap.containsKey(materialDto.getItemMaterialId())) {
            
                itemMaterial = existingMaterialsMap.get(materialDto.getItemMaterialId());
            
                if (!itemMaterial.getMaterial().getMaterialId().equals(materialDto.getMaterialId())) {
                    Material material = materialRepo.findById(materialDto.getMaterialId())
                        .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialDto.getMaterialId() + " is not found!"));
                    itemMaterial.setMaterial(material);
                }
            
                itemMaterial.setMaterialQuantity(materialDto.getMaterialQuantity());
            
            } 
            else {

                Material material = materialRepo.findById(materialDto.getMaterialId())
                    .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialDto.getMaterialId() + " is not found!"));

                itemMaterial = new BoqItemMaterial();
                itemMaterial.setMaterial(material);
                itemMaterial.setMaterialQuantity(materialDto.getMaterialQuantity());
                itemMaterial.setItem(item);
            }
        
            item.getMaterials().add(itemMaterial);
        }
    }

    @Override
    public BoqViewDto viewBoqById(Long boqId) {
        
        BillOfQuantities existingBoq = billOfQuantitiesRepo.findById(boqId)
        .orElseThrow(() -> new BoqNotFoundException("Cost Estimation ID: " + boqId + " is not found!"));

        BoqViewDto boqViewDto = new BoqViewDto();

        boqViewDto.setJobId(existingBoq.getJob().getJobId());
        boqViewDto.setBoqVersion(existingBoq.getBoqVersion());
        boqViewDto.setBoqStatus(existingBoq.getBoqStatus());

        List<BoqItemViewDto> itemViewDtoList = existingBoq.getItems().stream().map(item ->{

            BoqItemViewDto itemViewDto = new BoqItemViewDto();

            itemViewDto.setItemId(item.getItemId());
            itemViewDto.setItemName(item.getItemName());
            itemViewDto.setItemQuantity(item.getItemQuantity());

            List<BoqItemMaterialViewDto> itemMaterialViewDtoList = item.getMaterials().stream().map(itemMaterial ->{

                BoqItemMaterialViewDto itemMaterialViewDto = new BoqItemMaterialViewDto();

                itemMaterialViewDto.setItemMaterialId(itemMaterial.getItemMaterialId());
                itemMaterialViewDto.setMaterialName(itemMaterial.getMaterial().getMaterialName());
                itemMaterialViewDto.setMaterialId(itemMaterial.getMaterial().getMaterialId());
                itemMaterialViewDto.setMaterialQuantity(itemMaterial.getMaterialQuantity());
                itemMaterialViewDto.setSectionId(MaterialTypeUtil.generateSectionId(itemMaterial.getMaterial().getMaterialType()));
                itemMaterialViewDto.setMaterialDescription(itemMaterial.getMaterial().getMaterialDescription());

                return itemMaterialViewDto;

            }).collect(Collectors.toList());

            itemViewDto.setItemMaterials(itemMaterialViewDtoList);

            return itemViewDto;
        }).collect(Collectors.toList());

        boqViewDto.setItems(itemViewDtoList);

        return boqViewDto;
    }

    @Override
    @Transactional
    public void addCustomerDesign(Long boqId, BoqDesignDto boqDesignDto) {
        
        BillOfQuantities billOfQuantities = billOfQuantitiesRepo.findById(boqId)
                .orElseThrow(() -> new DesignProcessingException("BOQ ID: " + boqId + " is not found!"));

        if (billOfQuantities.getBoqStatus() != BoqStatus.READY_TO_SUBMIT) {
            throw new InvalidStatusException("Adding customer design assets requires a completed BOQ!");
        }

        List<CustomerDesignDto> designDtos = boqDesignDto.getCustomerDesigns();
        if (designDtos == null) {
            return;
        }

        for (CustomerDesignDto designDto : designDtos) {
            Long designId = designDto.getCustomerDesignId();
            MultipartFile designFile = designDto.getCustomerDesign();

            try {
                if (designId == null && designFile != null && !designFile.isEmpty()) {

                    CustomerDesign newDesign = new CustomerDesign();
                    newDesign.setBillOfQuantities(billOfQuantities);
                    newDesign.setCDesignname(designFile.getOriginalFilename());
                    newDesign.setCDesignType(designFile.getContentType());
                    newDesign.setCDesignSize(designFile.getSize());
                    newDesign.setCDesignData(designFile.getBytes());
                    customerDesignRepo.save(newDesign);

                } else if (designId != null && designFile != null && !designFile.isEmpty()) {

                    Optional<CustomerDesign> existingDesignOpt = customerDesignRepo.findById(designId);
                    if (existingDesignOpt.isPresent()) {
                        CustomerDesign existingDesign = existingDesignOpt.get();
                        if (existingDesign.getBillOfQuantities().getBoqId().equals(boqId)) {
                            existingDesign.setCDesignname(designFile.getOriginalFilename());
                            existingDesign.setCDesignType(designFile.getContentType());
                            existingDesign.setCDesignSize(designFile.getSize());
                            existingDesign.setCDesignData(designFile.getBytes());
                            customerDesignRepo.save(existingDesign);
                        } else {
                            throw new DesignProcessingException("Design ID: " + designId + " does not belong to BOQ ID: " + boqId);
                        }
                    } else {
                        throw new DesignProcessingException("Customer Design not found with ID: " + designId);
                    }

                } else if (designId != null && (designFile == null || designFile.isEmpty())) {

                    Optional<CustomerDesign> existingDesignOpt = customerDesignRepo.findById(designId);
                    if (existingDesignOpt.isPresent()) {
                        CustomerDesign existingDesign = existingDesignOpt.get();
                        if (existingDesign.getBillOfQuantities().getBoqId().equals(boqId)) {
                            customerDesignRepo.delete(existingDesign);
                        } else {
                            throw new DesignProcessingException("Design ID: " + designId + " does not belong to BOQ ID: " + boqId);
                        }
                    } else {
                        throw new DesignProcessingException("Customer Design ID: " + designId + " is not found!");
                    }
                }
            } catch (IOException e) {
                throw new DesignProcessingException("Failed to process design file for ID: " + designId, e);
            }
        }
    }

    @Override
    public CustomerDesignAssetViewDto viewCustomerDesignByBoqId(Long boqId) {
        
        BillOfQuantities billOfQuantities = billOfQuantitiesRepo.findById(boqId)
            .orElseThrow(() -> new DesignProcessingException("BOQ ID: " + boqId + " is not found!"));

        CustomerDesignAssetViewDto customerDesignAssetViewDto = new CustomerDesignAssetViewDto();
        customerDesignAssetViewDto.setBoqStatus(billOfQuantities.getBoqStatus());

        if (billOfQuantities.getCustomerDesigns() != null) {

            List<CustomerDesign> customerDesigns = billOfQuantities.getCustomerDesigns();

            List<CustomerDesignViewDto> customerDesignViewDtoList = customerDesigns.stream().map(cdesign -> {

                CustomerDesignViewDto customerDesignViewDto = new CustomerDesignViewDto();

                String customerDesignUrl = CustomerDesignUrlUtil.constructCustomerDesignUrl(cdesign.getCDesignId());
                customerDesignViewDto.setCDesignId(cdesign.getCDesignId());
                customerDesignViewDto.setCDesignName(cdesign.getCDesignname());
                customerDesignViewDto.setCDesignUrl(customerDesignUrl);

                return customerDesignViewDto;

        }).collect(Collectors.toList());

        customerDesignAssetViewDto.setCustomerDesignViewDtoList(customerDesignViewDtoList);
        } 
        else {
            
        customerDesignAssetViewDto.setCustomerDesignViewDtoList(Collections.emptyList());
    }

    return customerDesignAssetViewDto;
    }

    @Override
    public ResponseEntity<byte[]> viewCustomerDesign(Long cDesignId, String disposition) {
        
        CustomerDesign customerDesign = customerDesignRepo.findById(cDesignId)
        .orElseThrow(() -> new CustomerDesignNotFoundException("Customer Design ID: " + cDesignId + " is not found or has no file data!"));

        String mimeType = customerDesign.getCDesignType() != null ? customerDesign.getCDesignType() : "application/octet-stream";

        String originalCustomerDesignName = customerDesign.getCDesignname();

        String contentDisposition;
        try {

            String encodedFilename = java.net.URLEncoder.encode(originalCustomerDesignName, "UTF-8")
                                .replace("+", "%20");
        
        contentDisposition = disposition + "; filename=\"" + originalCustomerDesignName + "\"; filename*=UTF-8''" + encodedFilename;
        } catch (Exception e) {

            contentDisposition = disposition + "; filename=\"" + originalCustomerDesignName + "\"";
        }
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mimeType))
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
            .body(customerDesign.getCDesignData());
    }

    @Override
    public BillOfQuantities handleBillOfQuantities(Long boqId) {
        
        BillOfQuantities billOfQuantities = billOfQuantitiesRepo.findById(boqId)
            .orElseThrow(() -> new DesignProcessingException("BOQ ID: " + boqId + " is not found!"));

        if (billOfQuantities.getBoqStatus() != BoqStatus.READY_TO_SUBMIT){

            throw new IllegalStatusTransitionException("Requires a completed BOQ to submit assets!");
        }

        billOfQuantities.setBoqStatus(BoqStatus.SUBMITTED);

        Job job = billOfQuantities.getJob();

        job.setJobStatus(JobStatus.READY_FOR_DESIGNING);
        jobRepo.save(job);

        BillOfQuantities updatedBillOfQuantities = billOfQuantitiesRepo.save(billOfQuantities);

        return updatedBillOfQuantities;
    }

    @Override
    public BoqTableViewDto viewSubmittedBoq(Long jobId) {
        
        Job job = jobRepo.findById(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job ID: " + jobId + " is not found!"));

        List<BillOfQuantities> billOfQuantities = billOfQuantitiesRepo.findByJob_JobIdAndBoqStatusOrderByBoqIdDesc(jobId, BoqStatus.SUBMITTED);

        BoqTableViewDto boqTableViewDto = new BoqTableViewDto();

        boqTableViewDto.setJobId(job.getJobId());
        boqTableViewDto.setProjectName(job.getEstimation().getInquiry().getProjectName());
        boqTableViewDto.setQuotationVersion(job.getEstimation().getQuotationVersion());

        List<BoqTableDto> boqTableDtoList = billOfQuantities.stream().map(boq -> {

            BoqTableDto boqTableDto = new BoqTableDto();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

            boqTableDto.setBoqId(boq.getBoqId());
            boqTableDto.setBoqVersion(boq.getBoqVersion());
            boqTableDto.setBoqStatus(boq.getBoqStatus());
            boqTableDto.setLastModifiedDate(boq.getUpdatedAt().format(formatter));

            if (boq.getCustomerDesigns() != null && !boq.getCustomerDesigns().isEmpty()) {
                
                boqTableDto.setCustomerDesignPresent(true);
            } 
            else {
                
                boqTableDto.setCustomerDesignPresent(false);
            }

            return boqTableDto;

        }).collect(Collectors.toList());

        boqTableViewDto.setBoqs(boqTableDtoList);

        return boqTableViewDto;

    }
}



        

        