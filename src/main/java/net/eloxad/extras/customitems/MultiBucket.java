package net.eloxad.extras.customitems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.eloxad.extras.utils.CustomItem;
import net.eloxad.extras.utils.LoreFormatter;
import net.eloxad.extras.utils.PDCHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class MultiBucket extends CustomItem {
    private final Map<Player, Long> isClicking = new HashMap<>();

    public MultiBucket() {
        super("multi_bucket", createItem(), "Multi Eimer");
        this.registerEvent(PlayerInteractEvent.class, this::onPlayerInteract);
        this.registerEvent(PlayerBucketEmptyEvent.class, this::onPlayerBucketEmpty);
        this.registerEvent(PlayerBucketFillEvent.class, this::onPlayerBucketFill);
        disableRenaming();
    }

    private static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.BUCKET);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Multi Eimer").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(
                Component.text("Dieser Eimer ist unendlich und kann zwischen ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("Leer", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("Wasser", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("Lava", TextColor.color(254, 67, 42)).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("wechseln.", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
        );
        lore = LoreFormatter.formatLore(40, lore);
        lore.add(Component.text("Modus ändern: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(Component.text("Sneak + Linksklick", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text("Aktueller Modus: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(Component.text("Leer", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        meta.lore(lore);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PDCHandler.set(container, "state", "empty");
        meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        return item;
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
            event.setCancelled(true);
            long currentTime = (new Date()).getTime();
            if (!this.isClicking.containsKey(player)) {
                this.isClicking.put(player, currentTime - 1000L);
            }

            long lastTime = (Long) this.isClicking.get(player);
            if (currentTime - lastTime < 1000L) {
                this.isClicking.put(player, currentTime);
                return;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            String[] states = new String[]{"empty", "water", "lava"};
            String[] names = new String[]{"Leer", "Wasser", "Lava"};
            TextColor[] colors = new TextColor[]{NamedTextColor.WHITE, NamedTextColor.BLUE, TextColor.color(254, 67, 42)};
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String state = (String) PDCHandler.get(container, "state");
            int index = Arrays.asList(states).indexOf(state);
            index = (index + 1) % 3;
            PDCHandler.set(container, "state", states[index]);
            List<Component> lore = meta.lore();
            lore.set(lore.size() - 1, ((TextComponent) Component.text("Aktueller Modus: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)).append(Component.text(names[index], colors[index]).decoration(TextDecoration.ITALIC, false)));
            meta.lore(lore);
            if (index == 0) {
                item = item.withType(Material.BUCKET);
            } else {
                item = item.withType(Material.valueOf(states[index].toUpperCase() + "_BUCKET"));
            }

            meta.setMaxStackSize(1);
            item.setItemMeta(meta);
            player.getInventory().setItemInMainHand(item);
            event.setCancelled(true);
            this.isClicking.put(player, currentTime);
        }

    }

    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getEquipment().getItem(event.getHand());
        event.setItemStack(item);
    }

    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getEquipment().getItem(event.getHand());
        event.setItemStack(item);
    }
}
