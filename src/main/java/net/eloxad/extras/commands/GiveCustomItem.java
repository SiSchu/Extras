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
            ItemStack item = this.customItemManager.getItem(args[0]).getItem();
            if (item == null) {
                player.sendRichMessage("<red>Item not found!");
                return true;
            }

            player.give(new ItemStack[]{item});
        }

        return true;
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> items = new ArrayList(this.customItemManager.getCustomItems().stream().map(CustomItem::getId).toList());
        String id = args[0];
        List<String> var7 = StringCheck.getStringStartsWith(id, (String[])items.toArray((x$0) -> new String[x$0]));
        return var7;
    }
}
