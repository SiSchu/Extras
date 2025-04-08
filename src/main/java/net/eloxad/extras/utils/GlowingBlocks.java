package net.eloxad.extras.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Shulker;

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
        return shulker;
    }

}
