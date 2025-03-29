package net.eloxad.extras.utils;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OfflineInventoryUtil {
    public static ItemStack[] loadArmor(UUID playerId) {
        // Load armor from playerdata file
        return new ItemStack[4];
    }
    public static ItemStack loadOffhand(UUID playerId) {
        // Load offhand from playerdata file
        return null;
    }
    public static ItemStack[] loadStorage(UUID playerId) {
        // Load main storage from playerdata file (36 slots)
        return new ItemStack[36];
    }
    public static void saveArmor(UUID playerId, ItemStack[] armor) {
        // Save armor back to playerdata file
    }
    public static void saveOffhand(UUID playerId, ItemStack offhand) {
        // Save offhand back to playerdata file
    }
    public static void saveStorage(UUID playerId, ItemStack[] storage) {
        // Save storage back to playerdata file
    }
}
