package net.eloxad.extras.managers;

import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Generated;
import net.eloxad.extras.utils.CustomItem;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@Singleton
public class CustomItemManager {
    private final List<CustomItem> customItems = new ArrayList();
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

    @Generated
    public List<CustomItem> getCustomItems() {
        return this.customItems;
    }

    @Generated
    public static CustomItemManager getInstance() {
        return instance;
    }
}
