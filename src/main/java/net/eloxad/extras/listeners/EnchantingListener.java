package net.eloxad.extras.listeners;

import net.eloxad.extras.utils.CustomItemModify;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantingListener implements Listener {
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        CustomItemModify.handleEnchanting(event);
    }
}
