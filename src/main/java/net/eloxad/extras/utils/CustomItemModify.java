package net.eloxad.extras.utils;

import net.eloxad.extras.Extras;
import net.eloxad.extras.managers.CustomItemManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Crafter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CrafterInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import javax.annotation.Nullable;
import java.util.Map;

public class CustomItemModify {

    public static void onAnvilClick(InventoryClickEvent event){
        if (event.getSlot() != 2) return;
        if (!(event.getInventory() instanceof AnvilInventory anvil)) return;
        ItemStack leftItem = anvil.getItem(0);
        ItemStack resultItem = anvil.getItem(2);
        if (leftItem == null || resultItem == null) return;
        Component leftDisplay = leftItem.hasItemMeta() && leftItem.getItemMeta().displayName() != null
                ? leftItem.getItemMeta().customName() : Component.empty();
        Component resultDisplay = resultItem.hasItemMeta() && resultItem.getItemMeta().customName() != null
                ? resultItem.getItemMeta().customName() : Component.empty();
        if (!leftDisplay.equals(resultDisplay)) {
            if (!CustomItemManager.getInstance().getOption(leftItem, "renaming")) {
                anvil.getViewers().forEach(p -> p.sendRichMessage("<red>Das Item kann nicht umbenannt werden!"));
                event.setCancelled(true);
                return;
            }
        }
        if (leftItem.hasItemMeta() && resultItem.hasItemMeta()
                && leftItem.getItemMeta() instanceof Damageable dLeft
                && resultItem.getItemMeta() instanceof Damageable dResult) {
            if (dLeft.getDamage() > dResult.getDamage()) {
                if (!CustomItemManager.getInstance().getOption(leftItem, "repairing")) {
                    anvil.getViewers().forEach(p -> p.sendRichMessage("<red>Das Item kann nicht repariert werden!"));
                    event.setCancelled(true);
                    return;
                }
            }
        }

        Map<Enchantment, Integer> leftEnchants = leftItem.getEnchantments();
        Map<Enchantment, Integer> resultEnchants = resultItem.getEnchantments();
        if (!resultEnchants.equals(leftEnchants)) {
            if (!CustomItemManager.getInstance().getOption(leftItem, "enchanting")) {
                anvil.getViewers().forEach(p -> p.sendRichMessage("<red>Das Item kann nicht verzaubert werden!"));
                event.setCancelled(true);
            }
        }
    }

    public static void handleCrafterCrafting(CrafterCraftEvent event) {
        BlockState blockState = event.getBlock().getState();
        Crafter crafter = (Crafter) blockState;
        event.setCancelled((handleCrafting(crafter.getInventory(), event.getBlock())));
    }

    public static void handlePlayerCrafting(CraftItemEvent event) {
        event.setCancelled((handleCrafting(event.getInventory(), null)));
    }

    public static boolean handleCrafting(Inventory inventory, @Nullable Block block) {
        boolean hasCustomItem = false;
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if(item.getType() != Material.AIR) {
                    if (!CustomItemManager.getInstance().getOption(item, "crafting_usage")) {
                        hasCustomItem = true;
                        break;
                    }
                }
            }
        }
        if (hasCustomItem) {
            if(block == null) {
                inventory.getViewers().forEach(p -> p.sendRichMessage("<red>Crafting blockiert: Ein oder mehrere Custom Items sind nicht zum weiter-craften erlaubt!"));
            }
            else {
                inventory.getViewers().forEach(p -> p.sendRichMessage("<red>Crafting blockiert: Ein oder mehrere Custom Items sind nicht zum weiter-craften erlaubt!"));
                Location location = block.getLocation();
                LivingEntity shulker = GlowingBlocks.spawnGlowingBlock(block);
                location.getNearbyPlayers(10).forEach(p -> p.playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.5F));
                Bukkit.getScheduler().runTaskLater(Extras.getInstance(), shulker::remove, 20L);
            }
            return true;
        }
        return false;
    }
}
