package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.customsets.RainbowArmor;
import net.eloxad.extras.managers.CustomItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinAndLeaveListener implements Listener {
    private final CustomItemManager customItemManager;

    @Inject
    public PlayerJoinAndLeaveListener(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        RainbowArmor.handle(event);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
    }
}
