package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import net.eloxad.extras.managers.CustomItemManager;
import net.eloxad.extras.managers.PlayerCacheManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityEquipmentChangeListener implements Listener {
    private final CustomItemManager customItemManager;
    private final PlayerCacheManager playerCacheManager;

    @Inject
    public EntityEquipmentChangeListener(CustomItemManager customItemManager, PlayerCacheManager playerCacheManager) {
        this.customItemManager = customItemManager;
        this.playerCacheManager = playerCacheManager;
    }

    @EventHandler
    public void onEntityEquipmentChange(EntityEquipmentChangedEvent event) {
    }
}
