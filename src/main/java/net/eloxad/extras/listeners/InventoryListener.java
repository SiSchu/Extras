package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.Extras;
import net.eloxad.extras.managers.InventorySessionManager;
import net.eloxad.extras.utils.CustomGUIHolder;
import net.eloxad.extras.utils.InventorySession;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        InventoryHolder topHolder = topInventory.getHolder();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if(clickedItem.getItemMeta() != null) {
                PersistentDataContainer dataContainer = clickedItem.getItemMeta().getPersistentDataContainer();
                NamespacedKey namespace = new NamespacedKey(Extras.getNamespace(), "inventory_sync_barrier");
                String syncBarrier = dataContainer.get(namespace, PersistentDataType.STRING);
                if (clickedItem.getType() == Material.BARRIER && (syncBarrier == "inventory_sync_barrier")) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (topHolder instanceof CustomGUIHolder customHolder) {
            if (!customHolder.getId().startsWith("player_inventory_")) return;

            UUID targetId;
            try {
                targetId = UUID.fromString(customHolder.getId().substring("player_inventory_".length()));
            } catch (IllegalArgumentException e) {
                return;
            }

            InventorySession session = InventorySessionManager.getSession(targetId);
            if (session == null) return;

            boolean isTarget = player.getUniqueId().equals(session.getTargetId());
            if (isTarget) {
                event.setCancelled(false);
                Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
            } else {
                event.setCancelled(false);
                Bukkit.getScheduler().runTask(Extras.getInstance(), () -> session.syncGUIToTarget(player));
            }
            return;
        }
        InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
        if (session != null && clickedInventory instanceof PlayerInventory) {
            event.setCancelled(false); // Allow normal edits
            Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        System.out.println("drag");
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        InventoryHolder topHolder = topInventory.getHolder();

        // If the observer clicked inside the GUI
        if (topHolder instanceof CustomGUIHolder customHolder) {
            if (!customHolder.getId().startsWith("player_inventory_")) return;

            String idStr = customHolder.getId().substring("player_inventory_".length());
            UUID targetId;
            try {
                targetId = UUID.fromString(idStr);
            } catch (IllegalArgumentException e) {
                return;
            }

            InventorySession session = InventorySessionManager.getSession(targetId);
            if (session == null) return;

            boolean isTarget = player.getUniqueId().equals(session.getTargetId());

            // Sync logic
            if (isTarget) {
                event.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(Extras.getInstance(), session::syncTargetToGUI, 1L);
            } else {
                event.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(Extras.getInstance(), () -> session.syncGUIToTarget(player), 1L);
            }
            return;
        }

        // If the player is clicking their own inventory (not as an observer)
        UUID targetId = player.getUniqueId();
        InventorySession session = InventorySessionManager.getSession(targetId);
        if (session != null) {
            event.setCancelled(false);
            Bukkit.getScheduler().runTaskLater(Extras.getInstance(), session::syncTargetToGUI, 1L);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
        if (session != null) {
            Bukkit.getScheduler().runTaskLater(Extras.getInstance(), session::syncTargetToGUI, 1L);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
        if (session != null) {
            Bukkit.getScheduler().runTaskLater(Extras.getInstance(), session::syncTargetToGUI, 1L);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof CustomGUIHolder customHolder && customHolder.getId().startsWith("player_inventory_")) {
            String idStr = customHolder.getId().substring("player_inventory_".length());
            UUID targetId = UUID.fromString(idStr);
            InventorySession session = InventorySessionManager.getSession(targetId);
            if (session != null) {
                session.addObserver(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof CustomGUIHolder customHolder && customHolder.getId().startsWith("player_inventory_")) {
            String idStr = customHolder.getId().substring("player_inventory_".length());
            UUID targetId = UUID.fromString(idStr);
            InventorySession session = InventorySessionManager.getSession(targetId);
            if (session != null) {
                session.removeObserver(player);
                if (session.getObservers().isEmpty()) {
                    InventorySessionManager.removeSession(targetId);
                }
            }
        }
    }
}
