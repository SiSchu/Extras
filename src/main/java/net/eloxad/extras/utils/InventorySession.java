package net.eloxad.extras.utils;

import lombok.Getter;
import net.eloxad.extras.Extras;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InventorySession {
    @Getter
    private final UUID targetId;
    @Getter
    private final boolean targetOnline;
    @Getter
    private final Inventory gui;
    @Getter
    private final Set<Player> observers = new HashSet<>();
    private boolean isSyncing = false;

    public InventorySession(OfflinePlayer target) {
        this.targetId = target.getUniqueId();
        this.targetOnline = target.isOnline();
        this.gui = Bukkit.createInventory(
                new CustomGUIHolder("player_inventory_" + targetId),
                45,
                Component.text(target.getName() + "'s Inventory")
        );
        syncTargetToGUI();
    }


    public void addObserver(Player observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            observer.openInventory(gui);
            observer.updateInventory();
        }
    }

    public void removeObserver(Player observer) {
        observers.remove(observer);
    }
    public void syncTargetToGUI() {
        if (isSyncing) return;
        isSyncing = true;

        ItemStack[] armor;
        ItemStack offhand;
        ItemStack[] storage;

        if (targetOnline) {
            Player target = Bukkit.getPlayer(targetId);
            if (target == null) {
                isSyncing = false;
                return;
            }
            armor = target.getInventory().getArmorContents();
            offhand = target.getInventory().getItemInOffHand();
            storage = target.getInventory().getStorageContents();

        } else {
            armor = OfflineInventoryUtil.loadArmor(targetId);
            offhand = OfflineInventoryUtil.loadOffhand(targetId);
            storage = OfflineInventoryUtil.loadStorage(targetId);
        }
        // Row 1 (slots 0-3): Armor
        for (int i = 0; i < 4; i++) {
            gui.setItem(3 - i, armor[i]);
        }
        // Slot 4: Offhand
        gui.setItem(4, offhand);
        // Slots 5-8: Barrier blocks
        ItemStack barrier = new ItemStack(org.bukkit.Material.BARRIER);
        org.bukkit.inventory.meta.ItemMeta meta = barrier.getItemMeta();
        meta.displayName(Component.empty());
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey namespace = new NamespacedKey(Extras.getNamespace(), "inventory_sync_barrier");
        dataContainer.set(namespace, PersistentDataType.STRING, "inventory_sync_barrier");
        barrier.setItemMeta(meta);
        for (int i = 5; i < 9; i++) {
            gui.setItem(i, barrier);
        }
        // Rows 2-4 (slots 9-35): Main inventory
        for (int i = 9; i < 36; i++) {
            gui.setItem(i, storage[i]);
        }
        // Row 5 (slots 36-44): Hotbar
        for (int i = 0; i < 9; i++) {
            gui.setItem(i + 36, storage[i]);
        }

        for (Player observer : observers) {
            if (observer.getOpenInventory().getTopInventory().equals(gui)) {
                observer.updateInventory();
            }
        }

        isSyncing = false;
    }

    public void syncGUIToTarget(Player sourceObserver) {
        if (sourceObserver.getUniqueId().equals(targetId)) return;
        if (isSyncing) return;
        isSyncing = true;

        ItemStack[] newArmor = new ItemStack[4];
        ItemStack newOffhand = gui.getItem(4);
        ItemStack[] newStorage = new ItemStack[36];
        for (int i = 0; i < 4; i++) {
            newArmor[i] = gui.getItem(3 - i);
        }

        for (int i = 9; i < 36; i++) {
            newStorage[i] = gui.getItem(i);
        }

        for (int i = 0; i < 9; i++) {
            newStorage[i] = gui.getItem(i + 36);
        }

        if (targetOnline) {
            Player target = Bukkit.getPlayer(targetId);
            if (target != null) {
                target.getInventory().setArmorContents(newArmor);
                target.getInventory().setItemInOffHand(newOffhand);
                target.getInventory().setStorageContents(newStorage);
            }
        } else {
            OfflineInventoryUtil.saveArmor(targetId, newArmor);
            OfflineInventoryUtil.saveOffhand(targetId, newOffhand);
            OfflineInventoryUtil.saveStorage(targetId, newStorage);
        }

        syncTargetToGUI();
        for (Player observer : observers) {
            if (!observer.getUniqueId().equals(sourceObserver.getUniqueId()) &&
                    observer.getOpenInventory().getTopInventory().equals(gui)) {
                observer.updateInventory();
            }
        }
        sourceObserver.updateInventory();

        isSyncing = false;
    }
}
