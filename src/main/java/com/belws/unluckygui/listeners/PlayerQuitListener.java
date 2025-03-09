package com.belws.unluckygui.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.logging.Logger;

public class PlayerQuitListener implements Listener {

    private final Logger logger;

    public PlayerQuitListener(Logger logger) {
        this.logger = logger;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // We don't need to explicitly clear mappings as MenuNavigator handles this with ConcurrentHashMap
        if (logger != null) {
            logger.info("Player " + player.getName() + " quit. Any menu sessions ended.");
        }
    }
}