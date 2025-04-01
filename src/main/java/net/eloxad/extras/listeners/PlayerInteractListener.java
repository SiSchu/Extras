package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.managers.CustomItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @Inject
    private CustomItemManager customItemManager;

    public PlayerInteractListener() {
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        this.customItemManager.handle(e, e.getItem());
    }
}
