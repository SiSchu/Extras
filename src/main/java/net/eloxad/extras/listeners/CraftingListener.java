package net.eloxad.extras.listeners;

import net.eloxad.extras.utils.CustomItemModify;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftingListener implements Listener {
    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        CustomItemModify.handlePlayerCrafting(event);
    }

    @EventHandler
    public void onCrafterCraft(CrafterCraftEvent event) {
        CustomItemModify.handleCrafterCrafting(event);
    }
}
