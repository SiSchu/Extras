package net.eloxad.extras.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import net.eloxad.extras.managers.CustomItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ExtrasModule extends AbstractModule {

    private final JavaPlugin plugin;

    public ExtrasModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(CustomItemManager.class).toInstance(new CustomItemManager());
    }

    @Provides
    public JavaPlugin providePlugin() {
        return plugin;
    }

}
