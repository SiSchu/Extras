package net.eloxad.extras.commands;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import net.eloxad.extras.managers.CustomItemManager;
import net.eloxad.extras.utils.CustomItem;
import net.eloxad.extras.utils.StringCheck;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GiveCustomItem implements CommandExecutor, TabCompleter {
    @Inject
    private CustomItemManager customItemManager;

    public GiveCustomItem() {
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            if(args.length == 1) {
                ItemStack item = customItemManager.getItem(args[0]).getItem();
                if (item == null) {
                    player.sendRichMessage("<red>Item not found!");
                    return true;
                }
                else{
                    player.getInventory().addItem(item);
                    return true;
                }
            } else {
                player.sendRichMessage("<red>Usage: /givecustomitem <id>");
            }
        }

        return true;
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> items = customItemManager.getCustomItems().stream().map(CustomItem::getId).toList();
        String id = args[0];
        items = StringCheck.getStringStartsWith(id, items.toArray(String[]::new));
        return items;
    }
}
