package com.belws.unluckygui.utils;

import com.belws.unluckygui.menus.MainMenu;
import com.belws.unluckygui.menus.ListMenu;
import com.belws.unluckygui.menus.PlayerOptionsMenu;
import com.belws.unluckygui.menus.HeldRolesMenu;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MenuNavigator {
    private static final Map<Player, MenuLevel> previousMenus = new ConcurrentHashMap<>();
    private static final Map<Player, MenuLevel> currentMenus = new ConcurrentHashMap<>();
    private static final Map<Player, Player> targetPlayers = new ConcurrentHashMap<>();

    private static LuckPermsHandler luckPermsHandler;

    // Constructor to inject LuckPermsHandler
    public MenuNavigator(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
    }

    public static void openMenu(Player viewer, MenuLevel menuLevel, Player target) {
        currentMenus.put(viewer, menuLevel);

        // Store target player if necessary
        if ((menuLevel == MenuLevel.PLAYER_OPTIONS || menuLevel == MenuLevel.HELD_ROLES_MENU) && target != null) {
            targetPlayers.put(viewer, target);
        }

        switch (menuLevel) {
            case MAIN_MENU -> viewer.openInventory(MainMenu.createMenu(viewer));
            case LIST_MENU -> viewer.openInventory(ListMenu.createMenu(viewer));
            case PLAYER_OPTIONS -> {
                // Use getTargetPlayer to get the correct target player (default to viewer if not set)
                Player targetPlayer = targetPlayers.getOrDefault(viewer, viewer);
                viewer.openInventory(PlayerOptionsMenu.createMenu(viewer, targetPlayer));
                System.out.println("Opened for "+ viewer+ " and " + targetPlayer);
            }
            case HELD_ROLES_MENU -> {
                Player targetPlayer = targetPlayers.getOrDefault(viewer, viewer);
                new HeldRolesMenu(luckPermsHandler).openMenu(viewer, targetPlayer); // Pass both viewer and target
            }
            default -> viewer.sendMessage("This menu is not available.");
        }
    }

    public static void goBack(Player player) {
        // Get the current menu level for the player
        MenuLevel currentMenuLevel = currentMenus.getOrDefault(player, MenuLevel.MAIN_MENU);

        Player targetPlayer = targetPlayers.getOrDefault(player, player);

        if (currentMenuLevel == MenuLevel.PLAYER_OPTIONS && !player.equals(targetPlayer)) {
            openMenu(player, MenuLevel.LIST_MENU, targetPlayer); // Go back to LIST_MENU
        }
        else if (currentMenuLevel == MenuLevel.PLAYER_OPTIONS && previousMenus.get(player) == MenuLevel.LIST_MENU) {
            openMenu(player, MenuLevel.LIST_MENU, targetPlayer); // Go back to LIST_MENU
        }
        else if (currentMenuLevel == MenuLevel.PLAYER_OPTIONS && previousMenus.get(player) == null) {
            player.closeInventory();
        }
        else {
            openMenu(player, MenuLevel.MAIN_MENU, targetPlayer); // Go back to MAIN_MENU
        }
    }

    public static Player getTargetPlayer(Player viewer) {
        return targetPlayers.getOrDefault(viewer, viewer);
    }
}

