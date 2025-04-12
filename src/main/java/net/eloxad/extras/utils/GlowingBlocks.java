package net.eloxad.extras.utils;

import net.eloxad.extras.Extras;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Shulker;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class GlowingBlocks {

    public static LivingEntity spawnGlowingBlock(Block block) {
        Location location = block.getLocation();
        LivingEntity shulker = block.getWorld().spawn(location, Shulker.class);
        shulker.setGlowing(true);
        shulker.setInvisible(true);
        shulker.setInvulnerable(true);
        shulker.setNoPhysics(true);
        shulker.setSilent(true);
        shulker.setAI(false);
        shulker.setCollidable(false);
        NamespacedKey key = new NamespacedKey(Extras.getNamespace(), "glowing_shulker");
        shulker.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        return shulker;
    }

    public static void onServerStart(JavaPlugin plugin){
        NamespacedKey key = new NamespacedKey(Extras.getNamespace(), "glowing_shulker");
        plugin.getServer().getWorlds().forEach(world -> {
            world.getLivingEntities().forEach(livingEntity -> {
                if(livingEntity.getPersistentDataContainer().has(key)){
                    livingEntity.remove();
                }
            });
        });
    }

}
