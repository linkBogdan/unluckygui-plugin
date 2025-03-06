package com.belws.unluckygui.commands;

import com.belws.unluckygui.menus.PlayerOptionsMenu; // Correct import for PlayerOptionsMenu
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;

public class OpenPlayerOptionsGui implements CommandExecutor {

    private final PlayerOptionsMenu playerOptionsMenu;

    public OpenPlayerOptionsGui() {
        this.playerOptionsMenu = new PlayerOptionsMenu(); // Initialize PlayerOptionsMenu
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Open the menu for the player themselves
            player.openInventory(playerOptionsMenu.createMenu(player, player)); // Pass both player and target (the player themselves)
        } else if (args.length == 1) {
            // Find the target player by name
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null && target.isOnline()) {
                // Open the menu for the target player
                player.openInventory(playerOptionsMenu.createMenu(player, target)); // Pass both player and target
            } else {
                sender.sendMessage("Player " + args[0] + " is not online or doesn't exist.");
            }
        } else {
            sender.sendMessage("Usage: /open <player_name>");
        }

        return true;
    }
}
