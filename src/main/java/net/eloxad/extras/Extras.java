package net.eloxad.extras;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import net.eloxad.extras.guice.ExtrasModule;
import net.eloxad.extras.register.RegisterCommands;
import net.eloxad.extras.register.RegisterCustomItems;
import net.eloxad.extras.register.RegisterListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class Extras extends JavaPlugin {
    @Getter
    private static Extras instance;
    @Getter
    private static final String namespace = "extras";
    @Getter
    private static Injector injector;

    public void onEnable() {
        instance = this;
        injector = Guice.createInjector(new ExtrasModule(this));
        RegisterListeners.register(this, injector);
        RegisterCommands.register(this, injector);
        RegisterCustomItems registerCustomItems = injector.getInstance(RegisterCustomItems.class);
        registerCustomItems.register(injector);
    }

    public void onDisable() {
    }


}
