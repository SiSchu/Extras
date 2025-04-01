package net.eloxad.extras.customsets;

import net.eloxad.extras.Extras;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class RainbowArmor {
    public static final String NAME = "rainbowarmor";

    public RainbowArmor() {
    }

    public static void handle(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        (new BukkitRunnable() {
            int offset = 0;

            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                } else {
                    int rgb = RainbowArmor.ColorUtils.rainbow(this.offset);

                    for(ItemStack item : player.getInventory().getArmorContents()) {
                        if (item != null && item.getItemMeta() != null) {
                            ItemMeta meta = item.getItemMeta();
                            PersistentDataContainer container = meta.getPersistentDataContainer();
                            NamespacedKey key = new NamespacedKey(Extras.getNamespace(), "rainbowarmor");
                            if (container.has(key, PersistentDataType.BOOLEAN)) {
                                LeatherArmorMeta leatherMeta = (LeatherArmorMeta)meta;
                                leatherMeta.setColor(Color.fromRGB(rgb));
                                item.setItemMeta(leatherMeta);
                            }
                        }
                    }

                    this.offset = (this.offset + 10) % 360;
                }
            }
        }).runTaskTimerAsynchronously(Extras.getInstance(), 0L, 2L);
    }

    private static class ColorUtils {
        private ColorUtils() {
        }

        static int rainbow(int offset) {
            float hue = (float)(offset % 360) / 360.0F;
            java.awt.Color awtColor = java.awt.Color.getHSBColor(hue, 1.0F, 1.0F);
            return awtColor.getRed() << 16 | awtColor.getGreen() << 8 | awtColor.getBlue();
        }
    }
}
