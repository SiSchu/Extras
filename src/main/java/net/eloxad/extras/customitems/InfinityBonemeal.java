package net.eloxad.extras.customitems;

import java.util.ArrayList;
import java.util.List;
import net.eloxad.extras.utils.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InfinityBonemeal extends CustomItem {
    public InfinityBonemeal() {
        super("infinity_bonemeal", createItem(), "Infinity Bonemeal");
        this.registerEvent(PlayerInteractEvent.class, this::onPlayerUseBonemeal);
    }

    private static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.BONE_MEAL);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Unendliches Knochenmehl").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Dieses Knochenmehl verbraucht sich nicht", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.setEnchantmentGlintOverride(true);
        meta.setMaxStackSize(1);
        item.setItemMeta(meta);
        return item;
    }

    public void onPlayerUseBonemeal(PlayerInteractEvent event) {
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if (action == Action.RIGHT_CLICK_BLOCK && block != null) {
            event.setUseItemInHand(Result.DENY);
            block.applyBoneMeal(event.getBlockFace());
        }
    }
}
