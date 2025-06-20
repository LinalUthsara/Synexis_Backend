package com.morphgen.synexis.service.serviceImpl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morphgen.synexis.dto.BoqItemMaterialViewDto;
import com.morphgen.synexis.dto.BoqItemViewDto;
import com.morphgen.synexis.dto.CostEstimationDto;
import com.morphgen.synexis.dto.CostEstimationForBOQViewDto;
import com.morphgen.synexis.dto.CostEstimationTableDto;
import com.morphgen.synexis.dto.CostEstimationTableViewDto;
import com.morphgen.synexis.dto.CostEstimationViewDto;
import com.morphgen.synexis.dto.ItemDto;
import com.morphgen.synexis.dto.ItemMaterialDto;
import com.morphgen.synexis.dto.ItemMaterialViewDto;
import com.morphgen.synexis.dto.ItemViewDto;
import com.morphgen.synexis.entity.CostEstimation;
import com.morphgen.synexis.entity.Inquiry;
import com.morphgen.synexis.entity.Item;
import com.morphgen.synexis.entity.ItemMaterial;
import com.morphgen.synexis.entity.Job;
import com.morphgen.synexis.entity.Material;
import com.morphgen.synexis.enums.Action;
import com.morphgen.synexis.enums.EstimationStatus;
import com.morphgen.synexis.exception.CostEstimationNotFoundException;
import com.morphgen.synexis.exception.IllegalStatusTransitionException;
import com.morphgen.synexis.exception.InquiryNotFoundException;
import com.morphgen.synexis.exception.InvalidStatusException;
import com.morphgen.synexis.exception.JobNotFoundException;
import com.morphgen.synexis.exception.MaterialNotFoundException;
import com.morphgen.synexis.repository.CostEstimationRepo;
import com.morphgen.synexis.repository.InquiryRepo;
import com.morphgen.synexis.repository.ItemMaterialRepo;
import com.morphgen.synexis.repository.ItemRepo;
import com.morphgen.synexis.repository.JobRepo;
import com.morphgen.synexis.repository.MaterialRepo;
import com.morphgen.synexis.service.ActivityLogService;
import com.morphgen.synexis.service.CostEstimationService;
import com.morphgen.synexis.utils.MaterialTypeUtil;

@Service

public class CostEstimationServiceImpl implements CostEstimationService {
    
    @Autowired
    private CostEstimationRepo costEstimationRepo;

    @Autowired
    private InquiryRepo inquiryRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired  
    private ItemMaterialRepo itemMaterialRepo;

    @Autowired
    private MaterialRepo materialRepo;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private JobRepo jobRepo;

    @Override
    @Transactional
    public CostEstimation createEstimation(CostEstimationDto costEstimationDto) {
        
        Inquiry inquiry = inquiryRepo.findById(costEstimationDto.getInquiryId())
        .orElseThrow(() -> new InquiryNotFoundException("Customer ID: " + costEstimationDto.getInquiryId() + " is not found!"));

        int currentCount = costEstimationRepo.countByInquiry_InquiryId(costEstimationDto.getInquiryId());

        String quotationVersion = inquiry.getQuotationNumber() + "/" + String.format("%03d", currentCount + 1);

        CostEstimation estimation = new CostEstimation();
        estimation.setInquiry(inquiry);
        estimation.setQuotationVersion(quotationVersion);
        estimation.setEstimationStatus(costEstimationDto.getEstimationStatus());
        estimation.setLabourRate(costEstimationDto.getLabourRate());
        estimation.setOtherCostRate(costEstimationDto.getOtherCostRate());

        List<Item> items = new ArrayList<>();

        for (ItemDto itemDto : costEstimationDto.getItems()) {
            Item item = new Item();
            item.setItemName(itemDto.getItemName());
            item.setItemQuantity(itemDto.getItemQuantity());
            item.setSwitchGearComponentMarkup(itemDto.getSwitchGearComponentMarkup());
            item.setControlAccessoryMarkup(itemDto.getControlAccessoryMarkup());
            item.setBusBarMarkup(itemDto.getBusBarMarkup());
            item.setWiringMarkup(itemDto.getWiringMarkup());
            item.setOtherAccessoryMarkup(itemDto.getOtherAccessoryMarkup());
            item.setElectricalLabourMarkup(itemDto.getElectricalLabourMarkup());
            item.setTransportMarkup(itemDto.getTransportMarkup());
            item.setEnclosureMarkup(itemDto.getEnclosureMarkup());
            item.setCostEstimation(estimation);

            List<ItemMaterial> itemMaterials = new ArrayList<>();
            for (ItemMaterialDto itemMaterialDto : itemDto.getItemMaterials()) {
                Material material = materialRepo.findById(itemMaterialDto.getMaterialId())
                .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + itemMaterialDto.getMaterialId() + " is not found!"));

                ItemMaterial itemMaterial = new ItemMaterial();
                itemMaterial.setMaterial(material);
                itemMaterial.setMaterialQuantity(itemMaterialDto.getMaterialQuantity());
                itemMaterial.setUnitPrice(itemMaterialDto.getUnitPrice());
                itemMaterial.setDiscount(itemMaterialDto.getDiscount());
                itemMaterial.setItem(item);

                itemMaterials.add(itemMaterial);
            }

            item.setMaterials(itemMaterials);
            items.add(item);
        }

        estimation.setItems(items);

        CostEstimation newCostEstimation = costEstimationRepo.save(estimation);

        activityLogService.logActivity(
            "CostEstimation", 
            newCostEstimation.getEstimationId(),
            newCostEstimation.getQuotationVersion(),
            Action.CREATE, 
            "Created Estimation: " + newCostEstimation.getQuotationVersion());

        return newCostEstimation;

    }

    @Override
    public CostEstimationTableViewDto viewEstimationTableByInquiryId(Long inquiryId) {

        Inquiry inquiry = inquiryRepo.findById(inquiryId)
        .orElseThrow(() -> new InquiryNotFoundException("Inquiry ID: " + inquiryId + " is not found!"));
        
        List<CostEstimation> costEstimations = costEstimationRepo.findByInquiry_InquiryIdOrderByEstimationIdDesc(inquiryId);

        CostEstimationTableViewDto costEstimationTableViewDto = new CostEstimationTableViewDto();

        costEstimationTableViewDto.setInquiryId(inquiryId);
        costEstimationTableViewDto.setQuotationNumber(inquiry.getQuotationNumber());

        List<CostEstimationTableDto> costEstimationTableDtoList = costEstimations.stream().map(estimation ->{

            CostEstimationTableDto costEstimationTableDto = new CostEstimationTableDto();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

            costEstimationTableDto.setEstimationId(estimation.getEstimationId());
            costEstimationTableDto.setQuotationVersion(estimation.getQuotationVersion());
            costEstimationTableDto.setEstimationStatus(estimation.getEstimationStatus());
            costEstimationTableDto.setLastModifiedDate(estimation.getUpdatedAt().format(formatter));

            return costEstimationTableDto;
        }).collect(Collectors.toList());

        costEstimationTableViewDto.setEstimations(costEstimationTableDtoList);
        
        return costEstimationTableViewDto;

    }

    @Override
    @Transactional
    public CostEstimation updateEstimation(Long estimationId, CostEstimationDto costEstimationDto) {
        
        CostEstimation existingEstimation = costEstimationRepo.findById(estimationId)
        .orElseThrow(() -> new CostEstimationNotFoundException("Cost Estimation ID: " + estimationId + " is not found!"));

        existingEstimation.setEstimationStatus(costEstimationDto.getEstimationStatus());
        existingEstimation.setLabourRate(costEstimationDto.getLabourRate());
        existingEstimation.setOtherCostRate(costEstimationDto.getOtherCostRate());
        
        if (existingEstimation.getItems() == null) {
            existingEstimation.setItems(new ArrayList<>());
        }
    
        Map<Long, Item> existingItemsMap = existingEstimation.getItems().stream()
            .filter(item -> item.getItemId() != null)
            .collect(Collectors.toMap(Item::getItemId, item -> item));

    
        Set<Long> updatedItemIds = new HashSet<>();
    
        for (ItemDto itemDto : costEstimationDto.getItems()) {
            if (itemDto.getItemId() != null) {
                updatedItemIds.add(itemDto.getItemId());
            }
        }

        List<Item> itemsToDelete = existingEstimation.getItems().stream()
            .filter(item -> item.getItemId() != null && !updatedItemIds.contains(item.getItemId()))
            .collect(Collectors.toList());

        for (Item itemToDelete : itemsToDelete) {

            if (itemToDelete.getMaterials() != null) {
                itemToDelete.getMaterials().clear();
            }

            existingEstimation.getItems().remove(itemToDelete);

            itemRepo.delete(itemToDelete);
        }

        existingEstimation.getItems().clear();
    

        for (ItemDto itemDto : costEstimationDto.getItems()) {
            
            Item item;
        
            if (itemDto.getItemId() != null && existingItemsMap.containsKey(itemDto.getItemId())) {

                item = existingItemsMap.get(itemDto.getItemId());
            

                item.setItemName(itemDto.getItemName());
                item.setItemQuantity(itemDto.getItemQuantity());
                item.setSwitchGearComponentMarkup(itemDto.getSwitchGearComponentMarkup());
                item.setControlAccessoryMarkup(itemDto.getControlAccessoryMarkup());
                item.setBusBarMarkup(itemDto.getBusBarMarkup());
                item.setWiringMarkup(itemDto.getWiringMarkup());
                item.setOtherAccessoryMarkup(itemDto.getOtherAccessoryMarkup());
                item.setElectricalLabourMarkup(itemDto.getElectricalLabourMarkup());
                item.setTransportMarkup(itemDto.getTransportMarkup());
                item.setEnclosureMarkup(itemDto.getEnclosureMarkup());
            } 
            else {
                
                item = new Item();
                item.setItemName(itemDto.getItemName());
                item.setItemQuantity(itemDto.getItemQuantity());
                item.setSwitchGearComponentMarkup(itemDto.getSwitchGearComponentMarkup());
                item.setControlAccessoryMarkup(itemDto.getControlAccessoryMarkup());
                item.setBusBarMarkup(itemDto.getBusBarMarkup());
                item.setWiringMarkup(itemDto.getWiringMarkup());
                item.setOtherAccessoryMarkup(itemDto.getOtherAccessoryMarkup());
                item.setElectricalLabourMarkup(itemDto.getElectricalLabourMarkup());
                item.setTransportMarkup(itemDto.getTransportMarkup());
                item.setEnclosureMarkup(itemDto.getEnclosureMarkup());
                item.setCostEstimation(existingEstimation);
            }


            updateItemMaterials(item, itemDto.getItemMaterials());
        
            existingEstimation.getItems().add(item);
        }

        CostEstimation updatedEstimation = costEstimationRepo.save(existingEstimation);

        activityLogService.logActivity(
        "CostEstimation", 
        updatedEstimation.getEstimationId(),
        updatedEstimation.getQuotationVersion(),
        Action.UPDATE, 
        "Updated Estimation: " + updatedEstimation.getQuotationVersion());

        return updatedEstimation;
    }

    private void updateItemMaterials(Item item, List<ItemMaterialDto> itemMaterialDtos) {
    
        if (item.getMaterials() == null) {
            item.setMaterials(new ArrayList<>());
        }

        Map<Long, ItemMaterial> existingMaterialsMap = item.getMaterials().stream()
            .filter(material -> material.getItemMaterialId() != null)
            .collect(Collectors.toMap(ItemMaterial::getItemMaterialId, material -> material));

        Set<Long> updatedMaterialIds = new HashSet<>();
    
        for (ItemMaterialDto materialDto : itemMaterialDtos) {
            if (materialDto.getItemMaterialId() != null) {
                updatedMaterialIds.add(materialDto.getItemMaterialId());
            }
        }

        List<ItemMaterial> materialsToDelete = item.getMaterials().stream()
            .filter(material -> material.getItemMaterialId() != null && !updatedMaterialIds.contains(material.getItemMaterialId()))
            .collect(Collectors.toList());

        for (ItemMaterial materialToDelete : materialsToDelete) {
            
            item.getMaterials().remove(materialToDelete);

            itemMaterialRepo.delete(materialToDelete);
        }

        item.getMaterials().clear();

        for (ItemMaterialDto materialDto : itemMaterialDtos) {
            ItemMaterial itemMaterial;
        
            if (materialDto.getItemMaterialId() != null && 
                existingMaterialsMap.containsKey(materialDto.getItemMaterialId())) {
            
                itemMaterial = existingMaterialsMap.get(materialDto.getItemMaterialId());
            
                if (!itemMaterial.getMaterial().getMaterialId().equals(materialDto.getMaterialId())) {
                    Material material = materialRepo.findById(materialDto.getMaterialId())
                        .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialDto.getMaterialId() + " is not found!"));
                    itemMaterial.setMaterial(material);
                }
            
                itemMaterial.setMaterialQuantity(materialDto.getMaterialQuantity());
                itemMaterial.setUnitPrice(materialDto.getUnitPrice());
                itemMaterial.setDiscount(materialDto.getDiscount());
            
            } 
            else {

                Material material = materialRepo.findById(materialDto.getMaterialId())
                    .orElseThrow(() -> new MaterialNotFoundException("Material ID: " + materialDto.getMaterialId() + " is not found!"));

                itemMaterial = new ItemMaterial();
                itemMaterial.setMaterial(material);
                itemMaterial.setMaterialQuantity(materialDto.getMaterialQuantity());
                itemMaterial.setUnitPrice(materialDto.getUnitPrice());
                itemMaterial.setDiscount(materialDto.getDiscount());
                itemMaterial.setItem(item);
            }
        
            item.getMaterials().add(itemMaterial);
        }
    }

    @Override
    public CostEstimationViewDto viewEstimationById(Long estimationId) {
        
        CostEstimation estimation = costEstimationRepo.findById(estimationId)
        .orElseThrow(() -> new CostEstimationNotFoundException("Cost Estimation ID: " + estimationId + " is not found!"));

        CostEstimationViewDto costEstimationViewDto = new CostEstimationViewDto();

        costEstimationViewDto.setInquiryId(estimation.getInquiry().getInquiryId());
        costEstimationViewDto.setQuotationVersion(estimation.getQuotationVersion());
        costEstimationViewDto.setEstimationStatus(estimation.getEstimationStatus());
        costEstimationViewDto.setLabourRate(estimation.getLabourRate());
        costEstimationViewDto.setOtherCostRate(estimation.getOtherCostRate());

        List<ItemViewDto> itemViewDtoList = estimation.getItems().stream().map(item ->{

            ItemViewDto itemViewDto = new ItemViewDto();

            itemViewDto.setItemId(item.getItemId());
            itemViewDto.setItemName(item.getItemName());
            itemViewDto.setItemQuantity(item.getItemQuantity());
            itemViewDto.setSwitchGearComponentMarkup(item.getSwitchGearComponentMarkup());
            itemViewDto.setControlAccessoryMarkup(item.getControlAccessoryMarkup());
            itemViewDto.setBusBarMarkup(item.getBusBarMarkup());
            itemViewDto.setWiringMarkup(item.getWiringMarkup());
            itemViewDto.setOtherAccessoryMarkup(item.getOtherAccessoryMarkup());
            itemViewDto.setElectricalLabourMarkup(item.getElectricalLabourMarkup());
            itemViewDto.setTransportMarkup(item.getTransportMarkup());
            itemViewDto.setEnclosureMarkup(item.getEnclosureMarkup());

            List<ItemMaterialViewDto> itemMaterialViewDtoList = item.getMaterials().stream().map(itemMaterial ->{

                ItemMaterialViewDto itemMaterialViewDto = new ItemMaterialViewDto();

                itemMaterialViewDto.setItemMaterialId(itemMaterial.getItemMaterialId());
                itemMaterialViewDto.setMaterialName(itemMaterial.getMaterial().getMaterialName());
                itemMaterialViewDto.setMaterialId(itemMaterial.getMaterial().getMaterialId());
                itemMaterialViewDto.setMaterialQuantity(itemMaterial.getMaterialQuantity());
                itemMaterialViewDto.setUnitPrice(itemMaterial.getUnitPrice());
                itemMaterialViewDto.setDiscount(itemMaterial.getDiscount());
                itemMaterialViewDto.setMaterialType(itemMaterial.getMaterial().getMaterialType());
                itemMaterialViewDto.setSectionId(MaterialTypeUtil.generateSectionId(itemMaterial.getMaterial().getMaterialType()));
                itemMaterialViewDto.setMaterialMarketPrice(itemMaterial.getMaterial().getMaterialMarketPrice());
                itemMaterialViewDto.setMaterialPartNumber(itemMaterial.getMaterial().getMaterialPartNumber());
                itemMaterialViewDto.setMaterialDescription(itemMaterial.getMaterial().getMaterialDescription());
                itemMaterialViewDto.setMaterialCountry(itemMaterial.getMaterial().getBrand().getBrandCountry());

                return itemMaterialViewDto;
            }).collect(Collectors.toList());

            itemViewDto.setItemMaterials(itemMaterialViewDtoList);

            return itemViewDto;
        }).collect(Collectors.toList());

        costEstimationViewDto.setItems(itemViewDtoList);

        return costEstimationViewDto;
    }

    @Override
    public CostEstimationTableViewDto viewEstimationApprovalTable(Long inquiryId) {
        
        Inquiry inquiry = inquiryRepo.findById(inquiryId)
        .orElseThrow(() -> new InquiryNotFoundException("Inquiry ID: " + inquiryId + " is not found!"));

        EstimationStatus estimationStatus = EstimationStatus.DRAFT;
        
        List<CostEstimation> costEstimations = costEstimationRepo.findByInquiry_InquiryIdAndEstimationStatusNotOrderByEstimationIdDesc(inquiryId, estimationStatus);

        CostEstimationTableViewDto costEstimationTableViewDto = new CostEstimationTableViewDto();

        costEstimationTableViewDto.setInquiryId(inquiryId);
        costEstimationTableViewDto.setQuotationNumber(inquiry.getQuotationNumber());

        List<CostEstimationTableDto> costEstimationTableDtoList = costEstimations.stream().map(estimation ->{

            CostEstimationTableDto costEstimationTableDto = new CostEstimationTableDto();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

            costEstimationTableDto.setEstimationId(estimation.getEstimationId());
            costEstimationTableDto.setQuotationVersion(estimation.getQuotationVersion());
            costEstimationTableDto.setEstimationStatus(estimation.getEstimationStatus());
            costEstimationTableDto.setLastModifiedDate(estimation.getUpdatedAt().format(formatter));

            return costEstimationTableDto;
        }).collect(Collectors.toList());

        costEstimationTableViewDto.setEstimations(costEstimationTableDtoList);
        
        return costEstimationTableViewDto;
    }

    @Override
    public CostEstimation handleEstimation(Long estimationId, EstimationStatus estimationStatus) {
        
        CostEstimation estimation = costEstimationRepo.findById(estimationId)
        .orElseThrow(() -> new CostEstimationNotFoundException("Cost Estimation ID: " + estimationId + " is not found!"));

        EstimationStatus existingStatus = estimation.getEstimationStatus();

        boolean isValidTransition = false;

        switch (existingStatus) {
            case SUBMITTED:
                isValidTransition = (estimationStatus == EstimationStatus.ACCEPTED || estimationStatus == EstimationStatus.REJECTED);
                break;
            case ACCEPTED:
                isValidTransition = (estimationStatus == EstimationStatus.REJECTED);
                break;
            case REJECTED:
                isValidTransition = (estimationStatus == EstimationStatus.ACCEPTED);
                break;
            default:
                throw new InvalidStatusException("Unknown existing status: " + existingStatus);
        }

        if (!isValidTransition) {
            throw new IllegalStatusTransitionException("Transition from " + existingStatus + " to " + estimationStatus + " is not valid!");
        }

        if (estimationStatus == EstimationStatus.ACCEPTED) {
        Long inquiryId = estimation.getInquiry().getInquiryId();
        boolean alreadyAcceptedExists = costEstimationRepo.existsByInquiry_InquiryIdAndEstimationStatus(inquiryId, EstimationStatus.ACCEPTED);

        if (alreadyAcceptedExists && existingStatus != EstimationStatus.ACCEPTED) {
            throw new IllegalStatusTransitionException(
                "Another estimation under " + estimation.getInquiry().getQuotationNumber() + " is already ACCEPTED.");
        }
    }

        estimation.setEstimationStatus(estimationStatus);

        CostEstimation updatedEstimation = costEstimationRepo.save(estimation);

        activityLogService.logActivity(
        "CostEstimation", 
        updatedEstimation.getEstimationId(),
        updatedEstimation.getQuotationVersion(),
        Action.UPDATE, 
        "Updated Estimation: " + updatedEstimation.getQuotationVersion());

        return updatedEstimation;
    }

    @Override
    public CostEstimationForBOQViewDto viewEstimationForBOQ(Long jobId) {
        
        Job job = jobRepo.findById(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job ID: " + jobId + " is not found!"));

        CostEstimation estimation = job.getEstimation();

        CostEstimationForBOQViewDto costEstimationForBOQViewDto = new CostEstimationForBOQViewDto();

        costEstimationForBOQViewDto.setJobId(job.getJobId());

        List<BoqItemViewDto> boqItemViewDtoList = estimation.getItems().stream().map(item ->{

            BoqItemViewDto boqItemViewDto = new BoqItemViewDto();

            boqItemViewDto.setItemId(item.getItemId());
            boqItemViewDto.setItemName(item.getItemName());
            boqItemViewDto.setItemQuantity(item.getItemQuantity());

            List<BoqItemMaterialViewDto> boqItemMaterialViewDtoList = item.getMaterials().stream().map(itemMaterial ->{

                BoqItemMaterialViewDto boqItemMaterialViewDto = new BoqItemMaterialViewDto();

                boqItemMaterialViewDto.setItemMaterialId(itemMaterial.getItemMaterialId());
                boqItemMaterialViewDto.setMaterialName(itemMaterial.getMaterial().getMaterialName());
                boqItemMaterialViewDto.setMaterialId(itemMaterial.getMaterial().getMaterialId());
                boqItemMaterialViewDto.setMaterialQuantity(itemMaterial.getMaterialQuantity());
                boqItemMaterialViewDto.setSectionId(MaterialTypeUtil.generateSectionId(itemMaterial.getMaterial().getMaterialType()));
                boqItemMaterialViewDto.setMaterialDescription(itemMaterial.getMaterial().getMaterialDescription());

                return boqItemMaterialViewDto;
            }).collect(Collectors.toList());

            boqItemViewDto.setItemMaterials(boqItemMaterialViewDtoList);

            return boqItemViewDto;
        }).collect(Collectors.toList());

        costEstimationForBOQViewDto.setItems(boqItemViewDtoList);

        return costEstimationForBOQViewDto;
    }
    

}

