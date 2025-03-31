package net.eloxad.extras.register;

import com.google.inject.Injector;
import net.eloxad.extras.commands.GiveCustomItem;
import net.eloxad.extras.commands.InvSeeCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterCommands {
    public static void register(JavaPlugin plugin, Injector injector) {
        plugin.getCommand("invsee").setExecutor(injector.getInstance(InvSeeCommand.class));
        plugin.getCommand("givecustomitem").setExecutor(injector.getInstance(GiveCustomItem.class));
        plugin.getCommand("givecustomitem").setTabCompleter(injector.getInstance(GiveCustomItem.class));
    }
}
