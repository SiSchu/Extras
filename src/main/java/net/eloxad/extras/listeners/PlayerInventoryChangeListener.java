package net.eloxad.extras.listeners;

import java.util.Objects;
import net.eloxad.extras.Extras;
import net.eloxad.extras.managers.CustomItemManager;
import net.eloxad.extras.managers.InventorySessionManager;
import net.eloxad.extras.utils.InventorySession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class PlayerInventoryChangeListener implements Listener {
    public PlayerInventoryChangeListener() {
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
        if (session != null) {
            Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
        if (session != null) {
            Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.PHYSICAL) {
            InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
            if (session != null) {
                Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
            }
        }
    }

    @EventHandler
    public void onItemDurabilityChange(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
        if (session != null) {
            Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
        }
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
        if (session != null) {
            Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
        if (session != null) {
            Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
        }
        ItemStack item = event.getItem();
        if(!CustomItemManager.getInstance().getOption(item, "enchanting")) {
            player.sendMessage("Das Item kann nicht verzaubert werden!");
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/give")) {
            Player player = event.getPlayer();
            InventorySession session = InventorySessionManager.getSession(player.getUniqueId());
            if (session == null) {
                return;
            }

            Bukkit.getScheduler().runTask(Extras.getInstance(), session::syncTargetToGUI);
        }

    }
}