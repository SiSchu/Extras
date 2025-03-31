package net.eloxad.extras.utils;

import net.eloxad.extras.Extras;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PDCHandler {
    public static Object get(PersistentDataContainer container, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(Extras.getNamespace(), key);
        if (!container.has(namespacedKey)) return null;
        if(container.has(namespacedKey, PersistentDataType.STRING)) return container.get(namespacedKey, PersistentDataType.STRING);
        else if (container.has(namespacedKey, PersistentDataType.INTEGER)) return container.get(namespacedKey, PersistentDataType.INTEGER);
        else if (container.has(namespacedKey, PersistentDataType.DOUBLE)) return container.get(namespacedKey, PersistentDataType.DOUBLE);
        else if (container.has(namespacedKey, PersistentDataType.BOOLEAN)) return container.get(namespacedKey, PersistentDataType.BOOLEAN);
        return null;
    }
    public static PersistentDataContainer set(PersistentDataContainer container, String key, Object value) {
        NamespacedKey namespacedKey = new NamespacedKey(Extras.getNamespace(), key);
        if (value instanceof String) container.set(namespacedKey, PersistentDataType.STRING, (String) value);
        else if (value instanceof Integer) container.set(namespacedKey, PersistentDataType.INTEGER, (Integer) value);
        else if (value instanceof Double) container.set(namespacedKey, PersistentDataType.DOUBLE, (Double) value);
        else if (value instanceof Boolean) container.set(namespacedKey, PersistentDataType.BOOLEAN, (Boolean) value);
        return container;
    }
}
