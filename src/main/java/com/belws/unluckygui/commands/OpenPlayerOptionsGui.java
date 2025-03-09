package com.belws.unluckygui.commands;

import com.belws.unluckygui.utils.MenuLevel;
import com.belws.unluckygui.utils.MenuNavigator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import java.util.logging.Logger;

public class OpenPlayerOptionsGui implements CommandExecutor {

    private final Logger logger;

    public OpenPlayerOptionsGui(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player targetPlayer;

        // If a player name is provided, try to find that player
        if (args.length > 0) {
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                player.sendMessage("§cPlayer not found: " + args[0]);
                return true;
            }

            // Store the target player in MenuNavigator
            MenuNavigator.targetPlayers.put(player.getUniqueId(), targetPlayer.getUniqueId());

            // Open the player options menu for the target directly using MenuNavigator
            MenuNavigator.openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
            logAction(player, targetPlayer, "player options menu");
        } else {
            // If no player is specified, use the player themselves as target
            targetPlayer = player;
            MenuNavigator.targetPlayers.put(player.getUniqueId(), targetPlayer.getUniqueId());
            MenuNavigator.openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
            logAction(player, targetPlayer, "self options menu");
        }

        return true;
    }

    private void logAction(Player player, Player target, String menuType) {
        if (logger != null) {
            logger.info("Player " + player.getName() + " opened " + menuType + " for " + target.getName());
        }
    }
}
