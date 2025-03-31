package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.managers.CustomItemManager;
import net.eloxad.extras.managers.PlayerCacheManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinAndLeaveListener implements Listener {
    private final CustomItemManager customItemManager;
    @Inject
    public PlayerJoinAndLeaveListener(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for(ItemStack item : event.getPlayer().getInventory().getArmorContents()) {
            if(item != null) customItemManager.handle(event, item);
        }
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        for(ItemStack item : event.getPlayer().getInventory().getArmorContents()) {
            if(item != null) customItemManager.handle(event, item);
        }
    }

}
