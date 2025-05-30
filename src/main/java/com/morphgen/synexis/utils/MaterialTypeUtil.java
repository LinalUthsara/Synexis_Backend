package com.morphgen.synexis.utils;

import java.util.HashMap;
import java.util.Map;

import com.morphgen.synexis.enums.MaterialType;

public class MaterialTypeUtil {
    
    private static final Map<MaterialType, Integer> materialTypeCodeMap = new HashMap<>();

    static {
        materialTypeCodeMap.put(MaterialType.SWITCH_GEAR_COMPONENTS, 1);
        materialTypeCodeMap.put(MaterialType.CONTROL_ACCESSORIES, 2);
        materialTypeCodeMap.put(MaterialType.BUSBAR, 3);
        materialTypeCodeMap.put(MaterialType.WIRING, 4);
        materialTypeCodeMap.put(MaterialType.OTHER_ACCESSORIES, 5);
        materialTypeCodeMap.put(MaterialType.ELECTRICAL_LABOR, 6);
        materialTypeCodeMap.put(MaterialType.TRANSPORT, 7);
        materialTypeCodeMap.put(MaterialType.ENCLOSURE, 8);
    }

    public static Integer generateSectionId(MaterialType type) {
        return materialTypeCodeMap.getOrDefault(type, -1);
    }
    
}
