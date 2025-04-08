package net.eloxad.extras.customsets;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import net.eloxad.extras.Extras;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RainbowArmor {
    private static final Map<UUID, BukkitTask> activeTasks = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> playerOffsets = new ConcurrentHashMap<>();

    public static void onArmorChange(Player player) {
        boolean hasArmor = hasRainbowArmor(player);
        if (hasArmor) {
            startRainbowEffect(player);
        } else {
            stopRainbowEffect(player);
        }
    }

    private static boolean hasRainbowArmor(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getItemMeta() instanceof LeatherArmorMeta meta) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(Extras.getNamespace(), "rainbowarmor");
                if (container.has(key, PersistentDataType.BOOLEAN)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void startRainbowEffect(Player player) {
        UUID uuid = player.getUniqueId();
        if (activeTasks.containsKey(uuid)) return;
        playerOffsets.put(uuid, new Random().nextInt(360));
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !hasRainbowArmor(player)) {
                    stopRainbowEffect(player);
                    return;
                }
                int offset = playerOffsets.get(uuid);
                int rgb = ColorUtils.rainbow(offset);
                for (ItemStack item : player.getInventory().getArmorContents()) {
                    if (item != null) {
                        item.editPersistentDataContainer(container -> {
                            if (container != null) {
                                NamespacedKey key = new NamespacedKey(Extras.getNamespace(), "rainbowarmor");
                                if (container.has(key, PersistentDataType.BOOLEAN)) {
                                    item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor().color(Color.fromRGB(rgb)));
                                    Equippable equippable = item.getData(DataComponentTypes.EQUIPPABLE);
                                    Key equipSound = equippable.equipSound();
                                    Equippable.Builder builder = equippable.toBuilder();
                                    if (player.getGameMode() == GameMode.CREATIVE) {
                                        Sound sound = Sound.sound(Key.key("intentionally_empty"), Sound.Source.PLAYER, 0.0F, 1.0F);
                                        if (equipSound != sound.name()) {
                                            builder.equipSound(sound.name());
                                            item.setData(DataComponentTypes.EQUIPPABLE, builder);
                                        }
                                    } else {
                                        Sound sound = Sound.sound(Key.key("item.armor.equip_leather"), Sound.Source.PLAYER, 1.0F, 1.0F);
                                        if (equipSound != sound.name()) {
                                            builder.equipSound(sound.name());
                                            item.setData(DataComponentTypes.EQUIPPABLE, builder);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                playerOffsets.put(uuid, (offset + 4) % 360);
            }
        }.runTaskTimerAsynchronously(Extras.getInstance(), 0L, 2L);
        activeTasks.put(uuid, task);
    }

    private static void stopRainbowEffect(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = activeTasks.remove(uuid);
        if (task != null) task.cancel();
        playerOffsets.remove(uuid);
    }

    public static void handleArmorChange(EntityEquipmentChangedEvent event) {
        if (event.getEntity() instanceof Player player) {
            event.getEquipmentChanges().forEach((slot, item) -> {
                if (!slot.isArmor()) return;
                ItemStack oldItem = item.oldItem();
                ItemStack newItem = item.newItem();
                boolean oldIsEmpty = oldItem.getType() == Material.AIR;
                boolean newIsEmpty = newItem.getType() == Material.AIR;
                if (oldIsEmpty != newIsEmpty) {
                    onArmorChange(player);
                    return;
                }
                ItemMeta oldMeta = oldItem.getItemMeta();
                ItemMeta newMeta = newItem.getItemMeta();
                if (newMeta == null || oldMeta == null) {
                    onArmorChange(player);
                    return;
                }
                NamespacedKey key = new NamespacedKey(Extras.getNamespace(), "rainbowarmor");
                PersistentDataContainer oldContainer = oldMeta.getPersistentDataContainer();
                PersistentDataContainer newContainer = newMeta.getPersistentDataContainer();
                boolean oldisRainbow = oldContainer.has(key, PersistentDataType.BOOLEAN);
                boolean newisRainbow = newContainer.has(key, PersistentDataType.BOOLEAN);
                if (oldisRainbow != newisRainbow) {
                    onArmorChange(player);
                }
            });
        }
    }

    private static class ColorUtils {
        static int rainbow(int offset) {
            float hue = (float) (offset % 360) / 360.0F;
            java.awt.Color awtColor = java.awt.Color.getHSBColor(hue, 1.0F, 1.0F);
            return awtColor.getRed() << 16 | awtColor.getGreen() << 8 | awtColor.getBlue();
        }
    }
}
