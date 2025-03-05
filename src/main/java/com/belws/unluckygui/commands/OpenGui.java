package com.belws.unluckygui.commands;

import com.belws.unluckygui.menus.MainMenu;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class OpenGui implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.openInventory(MainMenu.createMenu(player));
        }
        return true;
    }
}
