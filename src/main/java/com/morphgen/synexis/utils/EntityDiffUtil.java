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
                String fieldName = toPascalCase(field.getName());

                if (field.getType().equals(byte[].class)) {
                    byte[] oldBytes = (byte[]) oldValue;
                    byte[] newBytes = (byte[]) newValue;

                    boolean oldEmpty = (oldBytes == null || oldBytes.length == 0);
                    boolean newEmpty = (newBytes == null || newBytes.length == 0);

                    if (oldEmpty && !newEmpty) {
                        changes.add("Added " + fieldName);
                    } else if (!oldEmpty && newEmpty) {
                        changes.add("Removed " + fieldName);
                    } else if (!oldEmpty && !newEmpty && !Arrays.equals(oldBytes, newBytes)) {
                        changes.add("Updated " + fieldName);
                    }
                    continue;
                }

                boolean oldEmpty = isNullOrEmpty(oldValue);
                boolean newEmpty = isNullOrEmpty(newValue);

                if (oldEmpty && !newEmpty) {
                    changes.add("Added " + fieldName + ": " + newValue);
                } else if (!oldEmpty && newEmpty) {
                    changes.add("Removed " + fieldName + ": " + oldValue);
                } else if (!oldEmpty && !newEmpty && !oldValue.equals(newValue)) {
                    changes.add("Updated " + fieldName + " from " + oldValue + " to " + newValue);
                }

            } 
            catch (IllegalAccessException e) {
                changes.add("Could not access field: " + field.getName());
            }
        }

        return String.join(". ", changes).trim();
    }
    
    private static String toPascalCase(String fieldName) {
        
        String[] parts = fieldName.split("_");
        StringBuilder pascalCase = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                pascalCase.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    pascalCase.append(part.substring(1));
                }
            }
        }
        return pascalCase.toString();
    }

    private static boolean isNullOrEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof String) return ((String) obj).trim().isEmpty();
        return false;
    }
}
