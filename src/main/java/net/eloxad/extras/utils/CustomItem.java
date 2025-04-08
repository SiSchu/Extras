package net.eloxad.extras.utils;

import lombok.Getter;
import net.eloxad.extras.Extras;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class CustomItem {
    @Getter
    private final String id;
    @Getter
    private final ItemStack item;
    @Getter
    private final String itemName;
    private final Map<Class<? extends Event>, Consumer<? extends Event>> eventHandlers = new HashMap<>();
    private final NamespacedKey key;
    private final Map<String, Map<String, Boolean>> options = new HashMap<>();

    public CustomItem(String id, ItemStack item, String itemName) {
        this.id = id;
        this.item = item;
        this.key = new NamespacedKey(Extras.getNamespace(), "custom_item_id");
        this.itemName = itemName;
        setCustomID(item, id);
    }


    protected <T extends Event> void registerEvent(Class<T> eventClass, Consumer<T> handler) {
        eventHandlers.put(eventClass, handler);
    }


    public void handleEvent(Event event, ItemStack usedItem) {
        if (!matchesItem(usedItem)) return;
        @SuppressWarnings("unchecked")
        Consumer<Event> handler = (Consumer<Event>) eventHandlers.get(event.getClass());
        if (handler != null) {
            handler.accept(event);
        }
    }

    public boolean matchesItem(ItemStack other) {
        if (other == null || !other.hasItemMeta()) return false;
        ItemMeta meta = other.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(key, PersistentDataType.STRING)) return false;
        String storedId = container.get(key, PersistentDataType.STRING);
        return id.equals(storedId);
    }


    private void setCustomID(ItemStack item, String id) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
    }

    public boolean getOption(String id, String option) {
        if(!options.containsKey(id)) return true;
        return options.get(id).getOrDefault(option, true);
    }

    public boolean disableRenaming() {
        options.computeIfAbsent(id, k -> new HashMap<>()).put("renaming", false);
        return true;
    }

    public boolean disableEnchanting() {
        options.computeIfAbsent(id, k -> new HashMap<>()).put("enchanting", false);
        return true;
    }

    public boolean disableRepairing() {
        options.computeIfAbsent(id, k -> new HashMap<>()).put("repairing", false);
        return true;
    }

    public boolean disableCraftingUsage() {
        options.computeIfAbsent(id, k -> new HashMap<>()).put("crafting_usage", false);
        return true;
    }

}
