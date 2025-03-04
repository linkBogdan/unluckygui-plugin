package com.belws.unluckygui.utils;

import com.belws.unluckygui.menus.MainMenu;
import com.belws.unluckygui.menus.ListMenu;
import com.belws.unluckygui.menus.PlayerOptions;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MenuNavigator {

    private static final Map<Player, MenuLevel> previousMenus = new HashMap<>();
    private static final Map<Player, MenuLevel> currentMenus = new HashMap<>();
    private static final Map<Player, Player> targetPlayers = new HashMap<>();

    public static void openMenu(Player viewer, MenuLevel menuLevel, Player target) {
        // Store previous menu **only if moving deeper**
        if (currentMenus.containsKey(viewer) && menuLevel.getLevel() > currentMenus.get(viewer).getLevel()) {
            previousMenus.put(viewer, currentMenus.get(viewer));
        }

        // Update current menu level
        currentMenus.put(viewer, menuLevel);

        // If opening PlayerOptions, save the correct target player
        if (menuLevel == MenuLevel.PLAYER_OPTIONS && target != null) {
            targetPlayers.put(viewer, target);
        }

        // Open the corresponding menu
        switch (menuLevel) {
            case MAIN_MENU -> viewer.openInventory(MainMenu.createMenu(viewer));
            case LIST_MENU -> viewer.openInventory(ListMenu.createMenu(viewer));
            case PLAYER_OPTIONS -> {
                Player targetPlayer = targetPlayers.getOrDefault(viewer, viewer);
                viewer.openInventory(PlayerOptions.createMenu(targetPlayer));
            }
            default -> viewer.sendMessage("This menu is not available.");
        }
    }

    public static void goBack(Player player) {
        // If there's a valid previous menu that is **lower in level**, navigate back
        if (previousMenus.containsKey(player) && previousMenus.get(player).getLevel() < currentMenus.get(player).getLevel()) {
            openMenu(player, previousMenus.get(player), targetPlayers.getOrDefault(player, player));
            return;
        }
        // If no valid previous menu, default to Main Menu
        openMenu(player, MenuLevel.MAIN_MENU, player);
    }

    public static Player getTargetPlayer(Player viewer) {
        return targetPlayers.getOrDefault(viewer, viewer);
    }
}
