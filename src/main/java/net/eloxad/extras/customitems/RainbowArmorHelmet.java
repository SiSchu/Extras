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

public class RainbowArmorHelmet extends CustomItem {
    public RainbowArmorHelmet() {
        super("rainbow_helmet", createItem(), "Regenbogen Helm");
        disableRenaming();
        disableCraftingUsage();
    }

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta meta = item.getItemMeta();
        Component name = LegacyComponentSerializer.legacyAmpersand().deserialize("&cR&6e&eg&ae&3n&9b&5o&cg&6e&en &3H&9e&5l&cm");
        meta.displayName(name.decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.setEnchantmentGlintOverride(true);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PDCHandler.set(container, "rainbowarmor", true);
        item.setItemMeta(meta);
        return item;
    }
}
