package net.eloxad.extras;

import lombok.Getter;
import net.eloxad.extras.commands.InvSeeCommand;
import net.eloxad.extras.listeners.InventoryListener;
import net.eloxad.extras.listeners.PlayerInventoryChangeListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Extras extends JavaPlugin {
    @Getter
    private static Extras instance;
    @Getter
    private static final String namespace = "extras";

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryChangeListener(), this);
        getCommand("invsee").setExecutor(new InvSeeCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
