package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.managers.CustomItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumeListener implements Listener {
    private final CustomItemManager customItemManager;
    @Inject
    public PlayerConsumeListener(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        customItemManager.handle(event, event.getItem());
    }

}
