package net.eloxad.extras.utils;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomGUIHolder implements InventoryHolder {
    @Getter
    private final String id;

    public CustomGUIHolder(String id) {
        this.id = id;
    }


    @Override
    public Inventory getInventory() {
        return null; // Not needed, just for identification
    }
}
