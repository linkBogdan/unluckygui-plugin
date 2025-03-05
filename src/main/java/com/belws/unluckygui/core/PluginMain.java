package com.belws.unluckygui.core;

import com.belws.unluckygui.commands.OpenGui;
import com.belws.unluckygui.commands.OpenRoleGui;
import com.belws.unluckygui.listeners.InventoryListener;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {

    private static PluginMain instance;
    private LuckPermsHandler luckPermsHandler;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize LuckPermsHandler only if LuckPerms plugin is found
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            getLogger().info("LuckPerms detected and hooked!");
            luckPermsHandler = new LuckPermsHandler(); // Initialize LuckPermsHandler
        } else {
            getLogger().warning("LuckPerms not found! Some features may be disabled.");
        }

        // Register the InventoryListener with the initialized luckPermsHandler instance
        if (luckPermsHandler != null) {
            getServer().getPluginManager().registerEvents(new InventoryListener(luckPermsHandler), this);
        }

        // Register commands
        this.getCommand("ul").setExecutor(new OpenGui());
        this.getCommand("role").setExecutor(new OpenRoleGui());  // Role menu command
    }

    public static PluginMain getInstance() {
        return instance;
    }

    public LuckPermsHandler getLuckPermsHandler() {
        return luckPermsHandler;
    }

    public void openMainMenu(Player player) {
        // Logic to open the main menu for the player
    }
}
