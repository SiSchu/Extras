package net.eloxad.extras.managers;

import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Generated;
import lombok.Getter;
import net.eloxad.extras.utils.CustomGUIHolder;
import net.eloxad.extras.utils.CustomItem;
import net.eloxad.extras.utils.PDCHandler;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@Singleton
public class CustomItemManager {
    @Getter
    private final List<CustomItem> customItems = new ArrayList<>();
    @Getter
    private static CustomItemManager instance;

    public CustomItemManager() {
        instance = this;
    }

    public void registerItem(CustomItem item) {
        this.customItems.add(item);
    }

    @Nullable
    public CustomItem getItem(String NameOrId) {
        for(CustomItem item : this.customItems) {
            if (item.getItemName().equals(NameOrId) || item.getId().equals(NameOrId)) {
                return item;
            }
        }
        return null;
    }

    public void handle(Event event, ItemStack heldItem) {
        for(CustomItem item : this.customItems) {
            if (item.matchesItem(heldItem)) {
                item.handleEvent(event, heldItem);
                break;
            }
        }
    }

    public boolean getOption(ItemStack item, String option) {
        if (!item.hasItemMeta()) return true;
        String id = (String) PDCHandler.get(item.getItemMeta().getPersistentDataContainer(), "custom_item_id");
        CustomItem customItem = getItem(id);
        if (customItem != null) {
            return customItem.getOption(id, option);
        }
        else return true;
    }


}
