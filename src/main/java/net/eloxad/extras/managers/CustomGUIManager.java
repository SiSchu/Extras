package net.eloxad.extras.managers;

import com.google.inject.Singleton;
import net.eloxad.extras.utils.CustomGUIHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Singleton
public class CustomGUIManager {
    private final Map<String, Map<Integer, Consumer<InventoryClickEvent>>> guis = new HashMap<>();
    private final Map<String, Inventory> guiRegistry = new HashMap<>();
    private final Map<String, Consumer<InventoryClickEvent>> bottomInventoryHandlers = new HashMap<>();

    public void addBottomInventoryHandler(String id, Consumer<InventoryClickEvent> handler) {
        bottomInventoryHandlers.put(id, handler);
    }

    public Inventory createGUI(String id, String title, int size) {
        CustomGUIHolder holder = new CustomGUIHolder(id);
        Inventory inventory = Bukkit.createInventory(holder, size, Component.text(title));
        guis.put(id, new HashMap<>());
        guiRegistry.put(id, inventory);
        return inventory;
    }

    public void addItem(Inventory inventory, ItemStack item, int slot) {
        inventory.setItem(slot, item);
    }

    public void addItem(Inventory inventory, ItemStack item, int slot, Consumer<InventoryClickEvent> action) {
        inventory.setItem(slot, item);
        if (inventory.getHolder() instanceof CustomGUIHolder holder) {
            guis.get(holder.getId()).put(slot, action);
        }
    }


    public void handleClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CustomGUIHolder holder) {
            String id = holder.getId();
            int rawSlot = event.getRawSlot();
            Map<Integer, Consumer<InventoryClickEvent>> slotActions = guis.get(id);
            if (rawSlot < event.getView().getTopInventory().getSize()) {
                if (slotActions != null) {
                    Consumer<InventoryClickEvent> action = slotActions.get(event.getSlot());
                    if (action != null) {
                        action.accept(event);
                    }
                }
            } else {
                Consumer<InventoryClickEvent> bottomHandler = bottomInventoryHandlers.get(id);
                if (bottomHandler != null) {
                    bottomHandler.accept(event);
                }
            }
        }
    }

    public void registerGUI(String id, Inventory inventory) {
        guiRegistry.put(id, inventory);
    }

    public Inventory getGUI(String id) {
        return guiRegistry.get(id);
    }
}
