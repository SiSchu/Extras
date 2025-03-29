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
    private final UUID targetId; // The target player's UUID
    @Getter
    private final boolean targetOnline; // Whether the target is online
    @Getter
    private final Inventory gui; // The shared GUI for all observers
    @Getter
    private final Set<Player> observers = new HashSet<>(); // Observers viewing the session
    private boolean isSyncing = false;     // Flag to prevent recursive syncing

    public InventorySession(OfflinePlayer target) {
        this.targetId = target.getUniqueId();
        this.targetOnline = target.isOnline();
        // Create a 45-slot GUI with a title indicating whose inventory is viewed.
        this.gui = Bukkit.createInventory(
                new CustomGUIHolder("player_inventory_" + targetId),
                45,
                Component.text(target.getName() + "'s Inventory")
        );
        // Perform an initial sync: load target inventory (or from file) into the GUI.
        syncTargetToGUI();
    }

    // --- Observer Management ---
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

    // --- Sync Methods ---
    // Sync target's inventory into the GUI and update all observers.
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
            // Load inventory data from file using your OfflineInventoryUtil.
            armor = OfflineInventoryUtil.loadArmor(targetId);
            offhand = OfflineInventoryUtil.loadOffhand(targetId);
            storage = OfflineInventoryUtil.loadStorage(targetId);
        }

        // Set GUI items:
        // Row 1 (slots 0-3): Armor (reversed order: slot 0 = boots, slot 3 = helmet)
        for (int i = 0; i < 4; i++) {
            gui.setItem(3 - i, armor[i]);
        }
        // Slot 4: Offhand
        gui.setItem(4, offhand);
        // Slots 5-8: Barrier blocks (non-editable placeholders)
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
        // Rows 2-4 (slots 9-35): Main inventory (player storage indices 9-35)
        for (int i = 9; i < 36; i++) {
            gui.setItem(i, storage[i]);
        }
        // Row 5 (slots 36-44): Hotbar (player storage indices 0-8)
        for (int i = 0; i < 9; i++) {
            gui.setItem(i + 36, storage[i]);
        }

        // Update all observers' GUIs.
        for (Player observer : observers) {
            if (observer.getOpenInventory().getTopInventory().equals(gui)) {
                observer.updateInventory();
            }
        }

        isSyncing = false;
    }

    // Sync GUI changes back to the target's inventory.
    // The sourceObserver parameter is the observer who initiated the change.
    // Changes from the target itself are not synced back (to avoid feedback loops).
    public void syncGUIToTarget(Player sourceObserver) {
        if (sourceObserver.getUniqueId().equals(targetId)) return; // Skip if target is the source.
        if (isSyncing) return;
        isSyncing = true;

        ItemStack[] newArmor = new ItemStack[4];
        ItemStack newOffhand = gui.getItem(4);
        ItemStack[] newStorage = new ItemStack[36];

        // Read armor from GUI slots 0-3 (reverse order)
        for (int i = 0; i < 4; i++) {
            newArmor[i] = gui.getItem(3 - i);
        }
        // Read main inventory from GUI slots 9-35 → indices 9-35
        for (int i = 9; i < 36; i++) {
            newStorage[i] = gui.getItem(i);
        }
        // Read hotbar from GUI slots 36-44 → indices 0-8
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
            // Save changes to file via your OfflineInventoryUtil.
            OfflineInventoryUtil.saveArmor(targetId, newArmor);
            OfflineInventoryUtil.saveOffhand(targetId, newOffhand);
            OfflineInventoryUtil.saveStorage(targetId, newStorage);
        }

        // After updating the target, re-sync the GUI so that all observers see the same updated state.
        syncTargetToGUI();
        // Update all observers except (optionally) the sourceObserver to prevent infinite loops.
        for (Player observer : observers) {
            if (!observer.getUniqueId().equals(sourceObserver.getUniqueId()) &&
                    observer.getOpenInventory().getTopInventory().equals(gui)) {
                observer.updateInventory();
            }
        }
        // Optionally, update the sourceObserver as well.
        sourceObserver.updateInventory();

        isSyncing = false;
    }
}
