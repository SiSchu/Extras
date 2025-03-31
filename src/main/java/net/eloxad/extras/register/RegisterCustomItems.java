package net.eloxad.extras.register;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.eloxad.extras.managers.CustomItemManager;
import net.eloxad.extras.utils.CustomItem;
import org.reflections.Reflections;

import java.util.Set;

public class RegisterCustomItems {
    private static CustomItemManager customItemManager;
    @Inject
    public RegisterCustomItems(CustomItemManager customItemManager) {
        RegisterCustomItems.customItemManager = customItemManager;
    }

    public void register(Injector injector) {
        String packageName = "net.eloxad.extras.customitems";
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends CustomItem>> allClasses = reflections.getSubTypesOf(CustomItem.class);
        for (Class<? extends CustomItem> clazz : allClasses) {
            customItemManager.registerItem(injector.getInstance(clazz));
        }
    }

}
