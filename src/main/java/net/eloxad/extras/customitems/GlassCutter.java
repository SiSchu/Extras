package net.eloxad.extras.customitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Multimap;
import net.eloxad.extras.utils.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GlassCutter extends CustomItem {
    public GlassCutter() {
        super("glass_cutter", createItem(), "glass_cutter");
        registerEvent(BlockDamageEvent.class, this::onBlockDamage);
    }

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.SHEARS);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Glasschneider").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Droppt Glas beim Abbauen", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Zerstört Glas instant", NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.setEnchantmentGlintOverride(true);
        meta.setUnbreakable(false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.EFFICIENCY, 15, true);
        item.setItemMeta(meta);
        return item;
    }



    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isGlass(block.getType())) {
            ItemStack shearitem = player.getEquipment().getItem(player.getActiveItemHand());
            shearitem.getItemMeta().getTool().setDamagePerBlock(0);
            ItemStack item = new ItemStack(block.getType());
            HashMap<Integer, ItemStack> drops = player.getInventory().addItem(item);
            if (!drops.isEmpty()) {
                for (ItemStack drop : drops.values()) {
                    block.getWorld().dropItem(block.getLocation(), drop);
                }
            }
            block.setType(Material.AIR);
            player.getWorld().playSound(block.getLocation(), "block.glass.break", 1.0F, 0.8F);
        }
    }

    public boolean isGlassBlock(Material material) {
        return material.toString().endsWith("GLASS");
    }

    public boolean isGlassPane(Material material) {
        return material.toString().endsWith("GLASS_PANE");
    }

    public boolean isGlass(Material material) {
        return this.isGlassBlock(material) || this.isGlassPane(material);
    }

    public boolean isTintedGlass(Material material) {
        return material.toString().endsWith("TINTED_GLASS");
    }
}
