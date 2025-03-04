package com.belws.unluckygui.core;

import com.belws.unluckygui.commands.OpenGui;
import com.belws.unluckygui.listeners.InventoryListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {

    private static PluginMain instance;

    @Override
    public void onEnable() {
        instance = this;

    getServer().getPluginManager().registerEvents(new InventoryListener(), this);

    this.getCommand("ul").setExecutor(new OpenGui());
    }

    public static PluginMain getInstance() {
        return instance;
    }

    public void openMainMenu(Player player) {
        // Logic to open the main menu for the player
    }
}
