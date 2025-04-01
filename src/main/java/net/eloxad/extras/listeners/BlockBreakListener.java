package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.managers.CustomItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;

public class BlockBreakListener implements Listener {
    private final CustomItemManager customItemManager;

    @Inject
    public BlockBreakListener(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.customItemManager.handle(event, event.getPlayer().getEquipment().getItemInMainHand());
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        this.customItemManager.handle(event, event.getPlayer().getEquipment().getItemInMainHand());
    }
}
