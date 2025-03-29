package net.eloxad.extras.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventorySync {


    // Sync target player's inventory to the GUI
    public static void syncTargetToGUI(Player target, Inventory gui) {
        ItemStack[] armor = target.getInventory().getArmorContents();
        ItemStack offhand = target.getInventory().getItemInOffHand();
        ItemStack[] storage = target.getInventory().getStorageContents();

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta = barrier.getItemMeta();
        meta.displayName(Component.empty()); // No name for the barrier blocks
        barrier.setItemMeta(meta);

// Set armor (row 1, slots 0-3)
        for (int i = 0; i < 4; i++) {
            gui.setItem(3 - i, armor[i]);  // Helmet -> Boots
        }

// Set offhand (slot 4)
        gui.setItem(4, offhand);

// Set barrier blocks (slots 6-8)
        for (int i = 5; i < 9; i++) {
            gui.setItem(i, barrier);
        }

// Sync inventory (rows 2-4, slots 9-35)
        for (int i = 9; i < 36; i++) {
            gui.setItem(i, storage[i]);
        }

        // Sync hotbar (row 5, slots 36-44)
        for (int i = 0; i < 9; i++) {
            gui.setItem(i + 36, storage[i]);  // Hotbar (slots 0-8)
        }
    }

    // Sync GUI back to target's inventory
    public static void syncGUIToTarget(Player target, Inventory gui) {
        ItemStack[] armor = new ItemStack[4];
        ItemStack offhand = null;
        ItemStack[] storage = new ItemStack[36];

        // Get armor from GUI (row 1, slots 0-3)
        for (int i = 0; i < 4; i++) {
            armor[i] = gui.getItem(3 - i);  // Helmet -> Boots
        }

        // Get offhand from GUI (slot 4)
        offhand = gui.getItem(4);

        // Sync inventory (rows 2-4, slots 9-35)
        for (int i = 9; i < 36; i++) {
            storage[i] = gui.getItem(i);  // Slot 9 in GUI goes to storage index 0, and so on
        }

        // Sync hotbar (row 5, slots 36-44, but only 0-8 for hotbar)
        for (int i = 36; i < 45; i++) {
            storage[i - 36] = gui.getItem(i);  // Map slot 36 in GUI to storage index 0, slot 37 to index 1, and so on
        }

        // Apply changes back to the player's inventory
        target.getInventory().setArmorContents(armor);
        target.getInventory().setItemInOffHand(offhand);
        target.getInventory().setStorageContents(storage);
    }
}






