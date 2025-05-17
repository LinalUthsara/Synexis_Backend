package com.morphgen.synexis.utils;

import com.morphgen.synexis.entity.Material;

public class BarcodeGenUtil {
    
    public static String generateBarcode(Material material){

        String brandId = String.format("%04d", material.getBrand().getBrandId());
        String categoryId = String.format("%04d", material.getCategory().getCategoryId());
        String materialId = String.format("%04d", material.getMaterialId());

        return String.join("", brandId, categoryId, materialId);
    }
}
