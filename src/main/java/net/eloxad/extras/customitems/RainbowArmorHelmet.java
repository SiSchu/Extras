package net.eloxad.extras.customitems;

import com.google.inject.Inject;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import net.eloxad.extras.managers.PlayerCacheManager;
import net.eloxad.extras.utils.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RainbowArmorHelmet extends CustomItem {
    private final PlayerCacheManager playerCacheManager;
    @Inject
    public RainbowArmorHelmet(PlayerCacheManager  playerCacheManager) {
        super("rainbow_helmet", createItem() , "rainbow_helmet");
        registerEvent(EntityEquipmentChangedEvent.class, this::onEntityEquipmentChange);
        registerEvent(PlayerJoinEvent.class, this::onPlayerJoin);
        registerEvent(PlayerQuitEvent.class, this::onPlayerLeave);
        this.playerCacheManager = playerCacheManager;
    }

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta meta = item.getItemMeta();
        Component name = LegacyComponentSerializer.legacyAmpersand().deserialize("&cR&6e&eg&ae&3n&9b&5o&cg&6e&en &3H&9e&5l&cm");
        meta.displayName(name.decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        return item;
    }

    public void onEntityEquipmentChange(EntityEquipmentChangedEvent event) {
        if(event.getEntity() instanceof Player player) {
            playerCacheManager.setValue(player.getUniqueId(), "rainbow_helmet", true);
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    public void onPlayerLeave(PlayerQuitEvent event) {

    }

}
