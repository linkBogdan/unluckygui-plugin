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
        currentMenus.put(viewerId, menuLevel); // Track the player's current menu

        // If the menu involves a target player, store that target in the map
        if ((menuLevel == MenuLevel.PLAYER_OPTIONS || menuLevel == MenuLevel.HELD_ROLES_MENU || menuLevel == MenuLevel.ROLE_ADD_MENU) && target != null) {
            targetPlayers.put(viewerId, target.getUniqueId());
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
                Player targetPlayer = getTargetPlayer(viewer);
                viewer.openInventory(PlayerOptionsMenu.createMenu(viewer, targetPlayer));
                break;
            case HELD_ROLES_MENU:
                targetPlayer = getTargetPlayer(viewer);
                new HeldRolesMenu(luckPermsHandler).openMenu(viewer, targetPlayer);
                break;
            case ROLE_ADD_MENU:
                targetPlayer = getTargetPlayer(viewer);
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

        // Navigate back based on the current menu level
        switch (currentMenuLevel) {
            case ROLE_ADD_MENU:
                openMenu(player, MenuLevel.HELD_ROLES_MENU, targetPlayer);
                break;
            case HELD_ROLES_MENU:
                openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
                break;
            case PLAYER_OPTIONS:
                if (!player.equals(targetPlayer)) {
                    openMenu(player, MenuLevel.LIST_MENU, targetPlayer);
                } else {
                    openMenu(player, MenuLevel.MAIN_MENU, targetPlayer);
                }
                break;
            default:
                openMenu(player, MenuLevel.MAIN_MENU, targetPlayer);
                break;
        }
    }

    // Method to get the target player for the viewer
    public static Player getTargetPlayer(Player viewer) {
        UUID targetUUID = targetPlayers.get(viewer.getUniqueId());
        if (targetUUID == null) {
            System.out.println("DEBUG: No target found for " + viewer.getName());
            return null;  // Or return the viewer themselves if no target player exists
        }
        Player targetPlayer = Bukkit.getPlayer(targetUUID); // Retrieve the player based on UUID
        System.out.println("DEBUG: Target for " + viewer.getName() + " is " + (targetPlayer != null ? targetPlayer.getName() : "null"));
        return targetPlayer;
    }

    // Optional: Utility method to clear a player's target
    public static void clearTarget(Player viewer) {
        targetPlayers.remove(viewer.getUniqueId());
    }

}
