package com.belws.unluckygui.core;

import com.belws.unluckygui.commands.OpenGui;
import com.belws.unluckygui.commands.OpenPlayerOptionsGui;  // Import the new command class
import com.belws.unluckygui.listeners.InventoryListener;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
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
        } else {
            getLogger().warning("LuckPerms not found! Some features may be disabled.");
        }

        if (luckPermsHandler != null) {
            getServer().getPluginManager().registerEvents(new InventoryListener(luckPermsHandler), this);
        }

        // Register the new /open command
        this.getCommand("ul").setExecutor(new OpenGui());
        this.getCommand("open").setExecutor(new OpenPlayerOptionsGui());
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
