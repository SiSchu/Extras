package net.eloxad.extras.listeners;

import java.util.Objects;
import java.util.UUID;

import com.google.inject.Inject;
import net.eloxad.extras.Extras;
import net.eloxad.extras.managers.CustomGUIManager;
import net.eloxad.extras.managers.InventorySessionManager;
import net.eloxad.extras.utils.CustomGUIHolder;
import net.eloxad.extras.utils.CustomItemModify;
import net.eloxad.extras.utils.InventorySession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

public class InventoryListener implements Listener {

    @Inject
    private CustomGUIManager customGUIManager;
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory() instanceof AnvilInventory) CustomItemModify.onAnvilClick(event);
        customGUIManager.handleClick(event);
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null) {
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            InventoryHolder topHolder = topInventory.getHolder();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getItemMeta() != null) {
                PersistentDataContainer dataContainer = clickedItem.getItemMeta().getPersistentDataContainer();
                NamespacedKey namespace = new NamespacedKey(Extras.getNamespace(), "inventory_sync_barrier");
                String syncBarrier = dataContainer.get(namespace, PersistentDataType.STRING);
                if (clickedItem.getType() == Material.BARRIER && syncBarrier != null && syncBarrier.equals("inventory_sync_barrier")) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (topHolder instanceof CustomGUIHolder customHolder) {
                if (customHolder.getId().startsWith("player_inventory_")) {
                    UUID targetId;
                    try {
                        targetId = UUID.fromString(customHolder.getId().substring("player_inventory_".length()));
                    } catch (IllegalArgumentException var11) {
                        return;
                    }

                    InventorySession session = InventorySessionManager.getSession(targetId);
                    if (session != null) {
                        boolean isTarget = player.getUniqueId().equals(session.getTargetId());
                        if (isTarget) {
                            event.setCancelled(false);
                            Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
                        } else {
                            event.setCancelled(false);
                            Bukkit.getScheduler().runTask(Extras.getInstance(), () -> session.syncGUIToTarget(player));
                        }

                    }
                }
            } else {
                InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
                if (session != null && clickedInventory instanceof PlayerInventory) {
                    event.setCancelled(false);
                    Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
                }

            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        InventoryHolder topHolder = topInventory.getHolder();
        if (topHolder instanceof CustomGUIHolder customHolder) {
            if (customHolder.getId().startsWith("player_inventory_")) {
                String idStr = customHolder.getId().substring("player_inventory_".length());

                UUID targetId;
                try {
                    targetId = UUID.fromString(idStr);
                } catch (IllegalArgumentException var10) {
                    return;
                }

                InventorySession session = InventorySessionManager.getSession(targetId);
                if (session != null) {
                    boolean isTarget = player.getUniqueId().equals(session.getTargetId());
                    if (isTarget) {
                        event.setCancelled(false);

                        Bukkit.getScheduler().runTaskLater(Extras.getInstance(), session::syncTargetToGUI, 1L);
                    } else {
                        event.setCancelled(false);
                        Bukkit.getScheduler().runTaskLater(Extras.getInstance(), () -> session.syncGUIToTarget(player), 1L);
                    }

                }
            }
        } else {
            UUID targetId = player.getUniqueId();
            InventorySession session = InventorySessionManager.getSession(targetId);
            if (session != null) {
                event.setCancelled(false);

                Bukkit.getScheduler().runTaskLater(Extras.getInstance(), session::syncTargetToGUI, 1L);
            }

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
        if (entity instanceof Player player) {
            InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
            if (session != null) {

                Bukkit.getScheduler().runTaskLater(Extras.getInstance(), session::syncTargetToGUI, 1L);
            }

        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof CustomGUIHolder customHolder) {
            if (customHolder.getId().startsWith("player_inventory_")) {
                String idStr = customHolder.getId().substring("player_inventory_".length());
                UUID targetId = UUID.fromString(idStr);
                InventorySession session = InventorySessionManager.getSession(targetId);
                if (session != null) {
                    session.addObserver(player);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof CustomGUIHolder customHolder) {
            if (customHolder.getId().startsWith("player_inventory_")) {
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
}
