package net.eloxad.extras.commands;

import net.eloxad.extras.managers.InventorySessionManager;
import net.eloxad.extras.utils.CustomGUIHolder;
import net.eloxad.extras.utils.InventorySession;
import net.eloxad.extras.utils.InventorySync;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvSeeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player executor)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        if(!executor.hasPermission("extras.invsee")) {
            executor.sendRichMessage("<red>You do not have permission to use this command!");
            return true;
        }
        if (args.length < 1) {
            executor.sendMessage("Usage: /editinv <target>");
            return true;
        }
        Player targetOnline = Bukkit.getPlayer(args[0]);
        OfflinePlayer target;
        if (targetOnline == null) {
            target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null || !target.hasPlayedBefore()) {
                executor.sendMessage("Player not found.");
                return true;
            }
        } else {
            target = targetOnline;
        }

        if(target == executor) {
            executor.sendMessage("You cannot view your own inventory.");
            return true;
        }
        InventorySession session = InventorySessionManager.getOrCreateSession(target);
        session.addObserver(executor);
        return true;
    }
}
