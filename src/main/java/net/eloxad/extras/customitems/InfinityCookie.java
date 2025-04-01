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

public class InfinityCookie extends CustomItem {
    public InfinityCookie() {
        super("infinity_cookie", createItem(), "Infinity Cookie");
        this.registerEvent(PlayerItemConsumeEvent.class, this::onPlayerItemConsume);
    }

    private static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.COOKIE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Unendlicher Cookie").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        List<Component> lore = new ArrayList();
        lore.add(Component.empty());
        lore.add(Component.text("Dieser Cookie ist unendlich", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Mit viel Liebe gebacken von: ", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false).append(Component.text("xXItzSiSchuXx", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)));
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
