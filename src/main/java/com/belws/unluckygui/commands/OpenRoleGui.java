package com.belws.unluckygui.commands;

import com.belws.unluckygui.core.PluginMain;
import com.belws.unluckygui.menus.HeldRolesMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;

public class OpenRoleGui implements CommandExecutor {

    private final HeldRolesMenu heldRolesMenu;

    public OpenRoleGui() {
        // Get the LuckPermsHandler from the main plugin class
        this.heldRolesMenu = new HeldRolesMenu(PluginMain.getInstance().getLuckPermsHandler());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // No player name given, open the role menu for the sender (the player)
            heldRolesMenu.openMenu(player, player);
        } else if (args.length == 1) {
            // A player name is given, try to find the player online
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null && target.isOnline()) {
                // Open the role menu for the specified player
                heldRolesMenu.openMenu(player, target);
                sender.sendMessage("Opened role menu for " + target.getName());
            } else {
                // Player not found or not online
                sender.sendMessage("Player " + args[0] + " is not online or doesn't exist.");
            }
        } else {
            // Too many arguments
            sender.sendMessage("Usage: /role <player_name>");
        }

        return true;
    }
}
