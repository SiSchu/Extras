package net.eloxad.extras.customitems;

import java.util.ArrayList;
import java.util.List;
import net.eloxad.extras.utils.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InfinityBeef extends CustomItem {
    public InfinityBeef() {
        super("infinity_beef", createItem(), "Infinity Beef");
        this.registerEvent(PlayerItemConsumeEvent.class, this::onPlayerItemConsume);
        disableRenaming();
    }

    private static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.COOKED_BEEF);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Unendliches Steak").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Dieses Steak ist unendlich", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        return item;
    }

    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getEquipment().getItem(event.getHand());
        player.getEquipment().setItem(event.getHand(), item);
    }
}
