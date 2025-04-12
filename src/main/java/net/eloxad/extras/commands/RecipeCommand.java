package net.eloxad.extras.commands;

import com.google.inject.Inject;
import net.eloxad.extras.managers.CustomGUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RecipeCommand implements CommandExecutor {
    @Inject
    private CustomGUIManager guiManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender instanceof Player player){
            player.openInventory(guiManager.getGUI("recipe_gui"));
        }
        return true;
    }
}
