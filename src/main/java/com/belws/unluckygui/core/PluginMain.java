package com.belws.unluckygui.core;

import com.belws.unluckygui.commands.OpenGui;
import com.belws.unluckygui.commands.OpenPlayerOptionsGui;
import com.belws.unluckygui.commands.ReloadCommand;
import com.belws.unluckygui.listeners.InventoryListener;
import com.belws.unluckygui.listeners.PlayerQuitListener;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import com.belws.unluckygui.menus.RoleAddMenu;
import com.belws.unluckygui.utils.MenuNavigator;
import com.belws.unluckygui.utils.RoleNameFormatter;
import com.belws.unluckygui.utils.RoleSettingsLoader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {

    private static PluginMain instance;
    private LuckPermsHandler luckPermsHandler;

    @Override
    public void onEnable() {
        instance = this;

        // Create data folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Check if LuckPerms is installed
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            getLogger().info("LuckPerms detected and hooked!");
            luckPermsHandler = new LuckPermsHandler();

            // Initialize RoleAddMenu with luckPermsHandler and a dummy player (replace with actual logic later)
            Player dummyPlayer = getServer().getPlayer("DummyPlayer");  // Replace this with actual logic to get the player
            RoleAddMenu roleAddMenu = new RoleAddMenu(luckPermsHandler, dummyPlayer);

            // Initialize MenuNavigator with LuckPermsHandler
            new MenuNavigator(luckPermsHandler);

            // Register the listeners with both LuckPermsHandler and RoleAddMenu
            getServer().getPluginManager().registerEvents(new InventoryListener(luckPermsHandler, roleAddMenu), this);
            getServer().getPluginManager().registerEvents(new PlayerQuitListener(getLogger()), this);
        } else {
            getLogger().warning("LuckPerms not found! Some features may be disabled.");
        }

        // Initialize the role formatter and settings loader
        RoleNameFormatter.loadRoles();
        RoleSettingsLoader.loadSettings();

        // Register commands with null check
        if (this.getCommand("ul") != null) {
            this.getCommand("ul").setExecutor(new OpenGui());
        } else {
            getLogger().warning("Command 'ul' not found.");
        }

        if (this.getCommand("open") != null) {
            this.getCommand("open").setExecutor(new OpenPlayerOptionsGui(getLogger()));
        } else {
            getLogger().warning("Command 'open' not found.");
        }

        // Create a single ReloadCommand instance for all reload commands
        ReloadCommand reloadCommand = new ReloadCommand(this);

        // Register all reload commands using the same executor
        if (this.getCommand("reload") != null) {
            this.getCommand("reload").setExecutor(reloadCommand);
        } else {
            getLogger().warning("Command 'reload' not found.");
        }

        if (this.getCommand("reloadroles") != null) {
            this.getCommand("reloadroles").setExecutor(reloadCommand);
        } else {
            getLogger().warning("Command 'reloadroles' not found.");
        }

        if (this.getCommand("reloadsettings") != null) {
            this.getCommand("reloadsettings").setExecutor(reloadCommand);
        } else {
            getLogger().warning("Command 'reloadsettings' not found.");
        }

        getLogger().info("UnluckyGUI has been enabled successfully!");
    }


    public static PluginMain getInstance() {
        return instance;
    }

    public LuckPermsHandler getLuckPermsHandler() {
        return luckPermsHandler;
    }

    @Override
    public void onDisable() {
        getLogger().info("UnluckyGUI has been disabled.");
    }
}
