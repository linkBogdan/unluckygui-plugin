package com.belws.unluckygui.utils;

import com.belws.unluckygui.menus.MainMenu;
import com.belws.unluckygui.menus.ListMenu;
import com.belws.unluckygui.menus.PlayerOptionsMenu;
import com.belws.unluckygui.menus.HeldRolesMenu;
import com.belws.unluckygui.menus.RoleAddMenu;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuNavigator {

    // Maps for managing menu navigation states and target players
    private static final Map<UUID, MenuLevel> previousMenus = new ConcurrentHashMap<>();
    private static final Map<UUID, MenuLevel> currentMenus = new ConcurrentHashMap<>();
    public static final Map<UUID, UUID> targetPlayers = new ConcurrentHashMap<>();

    private static LuckPermsHandler luckPermsHandler;

    // Constructor to initialize the LuckPermsHandler
    public MenuNavigator(LuckPermsHandler luckPermsHandler) {
        MenuNavigator.luckPermsHandler = luckPermsHandler;
    }

    // Method to open the specified menu for a player and assign a target
    public static void openMenu(Player viewer, MenuLevel menuLevel, Player target) {
        UUID viewerId = viewer.getUniqueId();
        MenuLevel previousMenu = currentMenus.get(viewerId);
        if (previousMenu != null) {
            previousMenus.put(viewerId, previousMenu);
        }
        currentMenus.put(viewerId, menuLevel);

        // Only update target if explicitly provided or if entering a menu that requires a target
        if (target != null && (menuLevel == MenuLevel.PLAYER_OPTIONS || 
            menuLevel == MenuLevel.HELD_ROLES_MENU || 
            menuLevel == MenuLevel.ROLE_ADD_MENU)) {
            targetPlayers.put(viewerId, target.getUniqueId());
        }

        // Ensure target player is fetched and valid
        Player targetPlayer = getTargetPlayer(viewer);
        if (targetPlayer == null && (menuLevel == MenuLevel.PLAYER_OPTIONS || 
            menuLevel == MenuLevel.HELD_ROLES_MENU || 
            menuLevel == MenuLevel.ROLE_ADD_MENU)) {
            viewer.sendMessage("No valid target player found.");
            return;
        }

        // Open the menu based on the specified level
        switch (menuLevel) {
            case MAIN_MENU:
                viewer.openInventory(MainMenu.createMenu(viewer));
                break;
            case LIST_MENU:
                viewer.openInventory(ListMenu.createMenu(viewer));
                break;
            case PLAYER_OPTIONS:
                viewer.openInventory(PlayerOptionsMenu.createMenu(viewer, targetPlayer));
                break;
            case HELD_ROLES_MENU:
                new HeldRolesMenu(luckPermsHandler).openMenu(viewer, targetPlayer);
                break;
            case ROLE_ADD_MENU:
                new RoleAddMenu(luckPermsHandler, targetPlayer).openMenu(viewer, targetPlayer);
                break;
            default:
                viewer.sendMessage("This menu is not available.");
                break;
        }
    }

    // Method to handle "Go Back" navigation by a player
    public static void goBack(Player player) {
        UUID playerId = player.getUniqueId();
        MenuLevel currentMenuLevel = currentMenus.getOrDefault(playerId, MenuLevel.MAIN_MENU);
        Player targetPlayer = getTargetPlayer(player);

        // Only allow going back from role management menus to player options
        switch (currentMenuLevel) {
            case ROLE_ADD_MENU:
            case HELD_ROLES_MENU:
                // Both role menus go back to player options
                openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
                break;
            default:
                // For all other menus, just close the inventory
                player.closeInventory();
                break;
        }
    }

    // Method to get the target player for the viewer
    public static Player getTargetPlayer(Player viewer) {
        UUID targetUUID = targetPlayers.get(viewer.getUniqueId());
        if (targetUUID == null) {
            targetPlayers.put(viewer.getUniqueId(), viewer.getUniqueId());
            return viewer;
        }
        return Bukkit.getPlayer(targetUUID);
    }

    // Optional: Utility method to clear a player's target
    public static void clearTarget(Player viewer) {
        targetPlayers.remove(viewer.getUniqueId());
    }
}
