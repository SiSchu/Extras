package net.eloxad.extras.commands;

import com.google.inject.Inject;
import net.eloxad.extras.managers.CustomItemManager;
import net.eloxad.extras.utils.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiveCustomItem implements CommandExecutor, TabCompleter {
    @Inject
    private CustomItemManager customItemManager;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender instanceof Player player) {
            ItemStack item = customItemManager.getItem(args[0]).getItem();
            if(item == null){
                player.sendRichMessage("<red>Item not found!");
                return true;
            }
            player.give(item);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> items = new ArrayList<>(customItemManager.getCustomItems().stream().map(CustomItem::getId).toList());
        return items;
    }
}
