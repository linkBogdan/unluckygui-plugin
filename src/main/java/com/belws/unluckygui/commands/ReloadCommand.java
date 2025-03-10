package com.belws.unluckygui.commands;

import com.belws.unluckygui.utils.RoleNameFormatter;
import com.belws.unluckygui.utils.RoleSettingsLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission for all reload commands
        if (!sender.hasPermission("unluckygui.reload")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        // Handle each command type based on the label (command name)
        switch (label.toLowerCase()) {
            case "reload":
                // Reload everything
                plugin.reloadConfig();
                RoleNameFormatter.loadRoles();
                RoleSettingsLoader.loadSettings();
                sender.sendMessage("§aPlugin reloaded successfully!");
                return true;

            case "reloadsettings":
                // Reload just the settings
                RoleNameFormatter.loadRoles();
                RoleSettingsLoader.loadSettings();
                sender.sendMessage("§aRole settings reloaded successfully!");
                return true;

            case "reloadroles":
                // Reload just the role names
                RoleNameFormatter.loadRoles();
                sender.sendMessage("§aRole names reloaded from roleFormat.yml!");
                return true;

            default:
                sender.sendMessage("§cUnknown reload command: " + label);
                return false;
        }
    }
}