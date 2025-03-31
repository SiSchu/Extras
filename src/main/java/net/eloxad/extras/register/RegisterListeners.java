package net.eloxad.extras.register;

import com.google.inject.Injector;
import net.eloxad.extras.listeners.BucketListener;
import net.eloxad.extras.listeners.InventoryListener;
import net.eloxad.extras.listeners.PlayerInventoryChangeListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class RegisterListeners {

    public static void register(JavaPlugin plugin, Injector injector) {
        String packageName = "net.eloxad.extras.listeners";
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends Listener>> allClasses = reflections.getSubTypesOf(Listener.class);
        for (Class<? extends Listener> clazz : allClasses) {
            plugin.getServer().getPluginManager().registerEvents(injector.getInstance(clazz), plugin);
        }
    }

}
