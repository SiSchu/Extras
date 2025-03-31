package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.managers.CustomItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @Inject
    private CustomItemManager customItemManager;
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        customItemManager.handle(e, e.getItem());
    }

}
