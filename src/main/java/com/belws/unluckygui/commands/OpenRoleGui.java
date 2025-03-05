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
        
        this.heldRolesMenu = new HeldRolesMenu(PluginMain.getInstance().getLuckPermsHandler());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            
            heldRolesMenu.openMenu(player, player);
        } else if (args.length == 1) {
            
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null && target.isOnline()) {
                
                heldRolesMenu.openMenu(player, target);
            } else {
                
                sender.sendMessage("Player " + args[0] + " is not online or doesn't exist.");
            }
        } else {
            
            sender.sendMessage("Usage: /role <player_name>");
        }

        return true;
    }
}
