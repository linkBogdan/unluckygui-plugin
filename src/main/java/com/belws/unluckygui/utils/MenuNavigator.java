package com.belws.unluckygui.utils;

import com.belws.unluckygui.menus.MainMenu;
import com.belws.unluckygui.menus.ListMenu;
import com.belws.unluckygui.menus.PlayerOptions;
import com.belws.unluckygui.menus.HeldRolesMenu;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class MenuNavigator {

    private static final Map<Player, MenuLevel> previousMenus = new HashMap<>();
    private static final Map<Player, MenuLevel> currentMenus = new HashMap<>();
    private static final Map<Player, Player> targetPlayers = new HashMap<>();
    private static LuckPermsHandler luckPermsHandler;

    // Menus that require LuckPerms handler (PLAYER_OPTIONS removed)
    private static final Set<MenuLevel> LUCKPERMS_REQUIRED_MENUS = new HashSet<>() {{
        add(MenuLevel.HELD_ROLES_MENU);
    }};

    // Method to set the LuckPermsHandler (typically called during plugin initialization)
    public static void setLuckPermsHandler(LuckPermsHandler handler) {
        luckPermsHandler = handler;
    }

    public static void openMenu(Player viewer, MenuLevel menuLevel, Player target) {
        // Check LuckPerms handler only for menus that require it
        if (LUCKPERMS_REQUIRED_MENUS.contains(menuLevel) && luckPermsHandler == null) {
            viewer.sendMessage("§cError: LuckPerms handler not initialized");
            return;
        }

        // Store previous menu **only if moving deeper**
        if (currentMenus.containsKey(viewer) && menuLevel.getLevel() > currentMenus.get(viewer).getLevel()) {
            previousMenus.put(viewer, currentMenus.get(viewer));
        }

        // Update current menu level
        currentMenus.put(viewer, menuLevel);

        // If opening PlayerOptions or HeldRoles, save the correct target player
        if ((menuLevel == MenuLevel.PLAYER_OPTIONS || menuLevel == MenuLevel.HELD_ROLES_MENU) && target != null) {
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
            case HELD_ROLES_MENU -> {
                Player targetPlayer = targetPlayers.getOrDefault(viewer, viewer);
                new HeldRolesMenu(luckPermsHandler).openMenu(viewer, targetPlayer);
            }
            case CONFIRMATION_MENU -> {
                // This is typically handled by the ConfirmationMenu class itself
                viewer.sendMessage("§cError: Use ConfirmationMenu to open this menu type.");
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
