package com.belws.unluckygui.core;

import com.belws.unluckygui.commands.OpenGui;
import com.belws.unluckygui.commands.OpenRoleGui;
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

        
        this.getCommand("ul").setExecutor(new OpenGui());
        this.getCommand("role").setExecutor(new OpenRoleGui());  
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
