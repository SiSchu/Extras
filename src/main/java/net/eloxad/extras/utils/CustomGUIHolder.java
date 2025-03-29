package net.eloxad.extras.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomGUIHolder implements InventoryHolder {
    private final String id;

    public CustomGUIHolder(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public Inventory getInventory() {
        return null; // Not needed, just for identification
    }
}
