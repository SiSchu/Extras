package net.eloxad.extras.listeners;

import com.google.inject.Inject;
import net.eloxad.extras.Extras;
import net.eloxad.extras.managers.CustomItemManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinAndLeaveListener implements Listener {
    private final CustomItemManager customItemManager;

    @Inject
    public PlayerJoinAndLeaveListener(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskTimerAsynchronously(Extras.getInstance(), () -> {
            if(player.activeBossBars().iterator().hasNext()) {
                player.activeBossBars().forEach(player::hideBossBar);
            }
            Double avgTickTime = player.getServer().getAverageTickTime();
            BossBar bossBar = BossBar.bossBar(Component.text( String.format("%.2f", avgTickTime) + " mspt"), 1.0f, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_20);
            event.getPlayer().showBossBar(bossBar);
        }, 0L, 5L);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
    }


}
