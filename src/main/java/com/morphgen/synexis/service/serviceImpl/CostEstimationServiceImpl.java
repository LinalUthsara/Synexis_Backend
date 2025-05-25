package com.morphgen.synexis.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.morphgen.synexis.dto.CostEstimationDto;
import com.morphgen.synexis.dto.ItemDto;
import com.morphgen.synexis.dto.ItemMaterialDto;
import com.morphgen.synexis.entity.CostEstimation;
import com.morphgen.synexis.entity.Item;
import com.morphgen.synexis.entity.ItemMaterial;
import com.morphgen.synexis.entity.Material;
import com.morphgen.synexis.exception.MaterialNotFoundException;
import com.morphgen.synexis.repository.CostEstimationRepo;
import com.morphgen.synexis.repository.MaterialRepo;
import com.morphgen.synexis.service.CostEstimationService;

@Service

public class CostEstimationServiceImpl implements CostEstimationService {
    
    @Autowired
    private CostEstimationRepo costEstimationRepo;

    @Autowired
    private MaterialRepo materialRepo;

    @Override
    public CostEstimation createEstimation(CostEstimationDto costEstimationDto) {
        
        CostEstimation estimation = new CostEstimation();
        estimation.setQuotationNumber(costEstimationDto.getQuotationNumber());
        estimation.setLabourRate(costEstimationDto.getLabourRate());

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

        return costEstimationRepo.save(estimation);
    }

}

