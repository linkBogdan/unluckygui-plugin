package com.belws.unluckygui.core;

import com.belws.unluckygui.commands.OpenGui;
import com.belws.unluckygui.commands.OpenPlayerOptionsGui;
import com.belws.unluckygui.listeners.InventoryListener;
import com.belws.unluckygui.listeners.PlayerQuitListener;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import com.belws.unluckygui.utils.MenuNavigator;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {

    private static PluginMain instance;
    private LuckPermsHandler luckPermsHandler;

    @Override
    public void onEnable() {
        instance = this;

        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            getLogger().info("LuckPerms detected and hooked!");
            luckPermsHandler = new LuckPermsHandler();

            // Initialize MenuNavigator with LuckPermsHandler
            new MenuNavigator(luckPermsHandler);

            // Register the listeners
            getServer().getPluginManager().registerEvents(new InventoryListener(luckPermsHandler), this);
            getServer().getPluginManager().registerEvents(new PlayerQuitListener(getLogger()), this);
        } else {
            getLogger().warning("LuckPerms not found! Some features may be disabled.");
        }

        // Register commands with logger access
        this.getCommand("ul").setExecutor(new OpenGui());
        this.getCommand("open").setExecutor(new OpenPlayerOptionsGui(getLogger()));
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