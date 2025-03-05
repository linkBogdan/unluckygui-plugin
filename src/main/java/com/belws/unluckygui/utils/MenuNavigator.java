package com.belws.unluckygui.utils;

import com.belws.unluckygui.menus.MainMenu;
import com.belws.unluckygui.menus.ListMenu;
import com.belws.unluckygui.menus.PlayerOptions;
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

    public static void openMenu(Player viewer, MenuLevel menuLevel, Player target) {
        // Store previous menu - only if moving deeper
        if (currentMenus.containsKey(viewer) && menuLevel.getLevel() > currentMenus.get(viewer).getLevel()) {
            previousMenus.put(viewer, currentMenus.get(viewer));
        }

        currentMenus.put(viewer, menuLevel);

        if ((menuLevel == MenuLevel.PLAYER_OPTIONS || menuLevel == MenuLevel.HELD_ROLES_MENU) && target != null) {
            targetPlayers.put(viewer, target);
        }


        switch (menuLevel) {
            case MAIN_MENU -> viewer.openInventory(MainMenu.createMenu(viewer));
            case LIST_MENU -> viewer.openInventory(ListMenu.createMenu(viewer));
            case PLAYER_OPTIONS -> {
                Player targetPlayer = targetPlayers.getOrDefault(viewer, viewer);
                viewer.openInventory(PlayerOptions.createMenu(targetPlayer));
            }
            case HELD_ROLES_MENU -> {
                Player targetPlayer = targetPlayers.getOrDefault(viewer, viewer);
                new HeldRolesMenu(luckPermsHandler).openMenu(viewer, targetPlayer);
            }
            case CONFIRMATION_MENU -> {
                viewer.sendMessage("§cError: Use ConfirmationMenu to open this menu type.");
            }
            default -> viewer.sendMessage("This menu is not available.");
        }
    }

    public static void goBack(Player player) {
        if (previousMenus.containsKey(player)) {
            openMenu(player, previousMenus.get(player), targetPlayers.getOrDefault(player, player));
        } else {
            openMenu(player, MenuLevel.MAIN_MENU, player);
        }
    }

    public static Player getTargetPlayer(Player viewer) {
        return targetPlayers.getOrDefault(viewer, viewer);
    }
}
