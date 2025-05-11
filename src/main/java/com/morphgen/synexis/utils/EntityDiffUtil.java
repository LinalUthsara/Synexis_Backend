package com.morphgen.synexis.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityDiffUtil {
    
    public static String describeChanges(Object oldObj, Object newObj) {
        if (!oldObj.getClass().equals(newObj.getClass())) {
            return "Cannot compare different entity types.";
        }

        List<String> changes = new ArrayList<>();
        Field[] fields = oldObj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);
                String fieldName = field.getName();

                if (field.getType().equals(byte[].class)) {
                    boolean changed = !Arrays.equals((byte[]) oldValue, (byte[]) newValue);
                    if (changed) {
                        changes.add("Changed '" + fieldName + "': image updated");
                    }
                    continue;
                }

                if (oldValue == null && newValue == null) continue;
                if (oldValue != null && oldValue.equals(newValue)) continue;
                if (newValue != null && newValue.equals(oldValue)) continue;

                changes.add("Changed '" + fieldName + "' from '" + oldValue + "' to '" + newValue + "'");
            } catch (IllegalAccessException e) {
                changes.add("Could not access field: " + field.getName());
            }
        }

        return String.join(". ", changes).trim();
    }
    
}
