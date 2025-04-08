package net.eloxad.extras.customitems;

import net.eloxad.extras.utils.CustomItem;
import net.eloxad.extras.utils.PDCHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class RainbowArmorChestplate extends CustomItem {
    public RainbowArmorChestplate() {
        super("rainbow_chestplate", createItem(), "Regenbogen Brustplatte");
        disableRenaming();
        disableCraftingUsage();
    }

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        Component name = LegacyComponentSerializer.legacyAmpersand().deserialize("&cR&6e&eg&ae&3n&9b&5o&cg&6e&en &3B&9r&5u&cs&6t&ep&al&3a&9t&5t&ce");
        meta.displayName(name.decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.setEnchantmentGlintOverride(true);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PDCHandler.set(container, "rainbowarmor", true);
        item.setItemMeta(meta);
        return item;
    }
}
