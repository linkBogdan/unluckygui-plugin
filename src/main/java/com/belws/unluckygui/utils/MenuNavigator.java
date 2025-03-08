package com.belws.unluckygui.utils;

import com.belws.unluckygui.menus.MainMenu;
import com.belws.unluckygui.menus.ListMenu;
import com.belws.unluckygui.menus.PlayerOptionsMenu;
import com.belws.unluckygui.menus.HeldRolesMenu;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import com.belws.unluckygui.utils.MenuLevel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuNavigator {
    private static final Map<UUID, MenuLevel> previousMenus = new ConcurrentHashMap<>();
    private static final Map<UUID, MenuLevel> currentMenus = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> targetPlayers = new ConcurrentHashMap<>();

    private static LuckPermsHandler luckPermsHandler;

    public MenuNavigator(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
    }

    public static void openMenu(Player viewer, MenuLevel menuLevel, Player target) {
        UUID viewerId = viewer.getUniqueId();
        currentMenus.put(viewerId, menuLevel);

        if ((menuLevel == MenuLevel.PLAYER_OPTIONS || menuLevel == MenuLevel.HELD_ROLES_MENU) && target != null) {
            targetPlayers.put(viewerId, target.getUniqueId());
        }

        switch (menuLevel) {
            case MAIN_MENU -> viewer.openInventory(MainMenu.createMenu(viewer));
            case LIST_MENU -> viewer.openInventory(ListMenu.createMenu(viewer));
            case PLAYER_OPTIONS -> {
                Player targetPlayer = getTargetPlayer(viewer);
                viewer.openInventory(PlayerOptionsMenu.createMenu(viewer, targetPlayer));
                System.out.println("Opened for " + viewer.getName() + " and target " + targetPlayer.getName());
            }
            case HELD_ROLES_MENU -> {
                Player targetPlayer = getTargetPlayer(viewer);
                new HeldRolesMenu(luckPermsHandler).openMenu(viewer, targetPlayer); // Pass both viewer and target
            }
            default -> viewer.sendMessage("This menu is not available.");
        }
    }

    public static void goBack(Player player) {
        UUID playerId = player.getUniqueId();
        MenuLevel currentMenuLevel = currentMenus.getOrDefault(playerId, MenuLevel.MAIN_MENU);
        Player targetPlayer = getTargetPlayer(player);

        if (currentMenuLevel == MenuLevel.HELD_ROLES_MENU) {
            openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
        } else if (currentMenuLevel == MenuLevel.PLAYER_OPTIONS && !player.equals(targetPlayer)) {
            openMenu(player, MenuLevel.LIST_MENU, targetPlayer);
        } else if (currentMenuLevel == MenuLevel.PLAYER_OPTIONS && previousMenus.get(playerId) == MenuLevel.LIST_MENU) {
            openMenu(player, MenuLevel.LIST_MENU, targetPlayer);
        } else {
            openMenu(player, MenuLevel.MAIN_MENU, targetPlayer);
        }
    }

    public static Player getTargetPlayer(Player viewer) {
        UUID targetUUID = targetPlayers.getOrDefault(viewer.getUniqueId(), viewer.getUniqueId());
        return Bukkit.getPlayer(targetUUID);
    }
}
