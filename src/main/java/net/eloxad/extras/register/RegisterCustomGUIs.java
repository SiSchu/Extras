package net.eloxad.extras.register;

import com.google.inject.Injector;
import net.eloxad.extras.interfaces.GUI;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.Set;

public class RegisterCustomGUIs {
    public static void register(JavaPlugin plugin, Injector injector) {
        String packageName = "net.eloxad.extras.guis";
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends GUI>> allClasses = reflections.getSubTypesOf(GUI.class);
        for (Class<? extends GUI > clazz : allClasses) {
            injector.getInstance(clazz);
        }
    }
}
