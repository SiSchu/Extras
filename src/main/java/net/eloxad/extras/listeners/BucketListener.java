package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.managers.CustomItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketListener implements Listener {
    private final CustomItemManager customItemManager;

    @Inject
    public BucketListener(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        this.customItemManager.handle(event, event.getPlayer().getEquipment().getItem(event.getHand()));
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        this.customItemManager.handle(event, event.getPlayer().getEquipment().getItem(event.getHand()));
    }
}
