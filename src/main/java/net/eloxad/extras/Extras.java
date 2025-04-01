package net.eloxad.extras;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.Generated;
import net.eloxad.extras.guice.ExtrasModule;
import net.eloxad.extras.register.RegisterCommands;
import net.eloxad.extras.register.RegisterCustomItems;
import net.eloxad.extras.register.RegisterListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class Extras extends JavaPlugin {
    private static Extras instance;
    private static final String namespace = "extras";
    private static Injector injector;

    public Extras() {
    }

    public void onEnable() {
        instance = this;
        injector = Guice.createInjector(new Module[]{new ExtrasModule(this)});
        RegisterListeners.register(this, injector);
        RegisterCommands.register(this, injector);
        RegisterCustomItems registerCustomItems = (RegisterCustomItems)injector.getInstance(RegisterCustomItems.class);
        registerCustomItems.register(injector);
    }

    public void onDisable() {
    }

    @Generated
    public static Extras getInstance() {
        return instance;
    }

    @Generated
    public static String getNamespace() {
        return "extras";
    }

    @Generated
    public static Injector getInjector() {
        return injector;
    }
}
