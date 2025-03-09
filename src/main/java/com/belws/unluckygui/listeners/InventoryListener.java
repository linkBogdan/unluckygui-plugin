package com.belws.unluckygui.listeners;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.belws.unluckygui.utils.MenuNavigator;
import com.belws.unluckygui.utils.MenuLevel;
import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryListener implements Listener {

    private final LuckPermsHandler luckPermsHandler;
    private final MenuNavigator menuNavigator;

    public InventoryListener(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
        this.menuNavigator = new MenuNavigator(luckPermsHandler);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        event.setCancelled(true);

        if (!(event.getInventory().getHolder() instanceof MenuHolder menuHolder)) return;
        MenuType menuType = menuHolder.getMenuType();

        // Retrieve the target player from the MenuNavigator's targetPlayers map
        UUID targetPlayerUUID = MenuNavigator.targetPlayers.get(player.getUniqueId());
        Player targetPlayer = targetPlayerUUID == null ? null : player.getServer().getPlayer(targetPlayerUUID);

        // Handle different menu interactions
        switch (menuType) {
            case MAIN_MENU:
                if (clickedItem.getType() == Material.BOOK) {
                    openMenu(player, MenuLevel.LIST_MENU, player);
                }
                break;

            case HELD_ROLES_MENU:
                if (clickedItem.getType() == Material.PAPER) {
                    if (targetPlayer != null) {
                        handleRoleManagement(player, clickedItem, targetPlayer, false);
                    } else {
                        player.sendMessage("§cError: Could not identify target player.");
                    }
                }
                break;

            case PLAYER_OPTIONS:
                if (targetPlayer != null) {
                    handlePlayerOptionsMenu(player, clickedItem, targetPlayer);
                } else {
                    player.sendMessage("§cError: Could not identify target player.");
                }
                break;

            case ROLE_ADD_MENU:
                if (clickedItem.getType() == Material.PAPER) {
                    if (targetPlayer != null) {
                        handleRoleManagement(player, clickedItem, targetPlayer, true);
                    } else {
                        player.sendMessage("§cError: Could not identify target player.");
                    }
                }
                break;
        }

        // Handle navigation elements (common across menu types)
        handleNavigationElements(player, clickedItem, menuType, targetPlayer);
    }

    private void handlePlayerOptionsMenu(Player player, ItemStack clickedItem, Player targetPlayer) {
        if (clickedItem.getType() == Material.DIRT) {
            openMenu(player, MenuLevel.ROLE_ADD_MENU, targetPlayer);
        } else {
            handleGameModeChange(player, clickedItem, targetPlayer);
        }
    }

    private void handleNavigationElements(Player player, ItemStack clickedItem, MenuType currentMenuType, Player targetPlayer) {
        // Go Back button
        if (clickedItem.getType() == Material.BARRIER && clickedItem.hasItemMeta()) {
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta != null && meta.displayName() != null &&
                    meta.displayName().equals(Component.text("Go Back", NamedTextColor.RED))) {
                MenuNavigator.goBack(player);
            }
        }

        // Player Head (Navigation)
        if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.hasItemMeta()) {
            handlePlayerHeadClick(player, currentMenuType, clickedItem);
        }
    }

    private void handleRoleManagement(Player player, ItemStack clickedItem, Player targetPlayer, boolean isAddition) {
        if (targetPlayer == null || clickedItem == null) {
            player.sendMessage("§cError: Could not identify target player.");
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || meta.displayName() == null) {
            return;
        }

        Component roleNameComponent = meta.displayName();
        String cleanedRoleName = PlainTextComponentSerializer.plainText().serialize(roleNameComponent).trim();

        if (!isAddition && cleanedRoleName.equalsIgnoreCase("default")) return;

        String roleName = isAddition ? cleanedRoleName : "group." + cleanedRoleName;

        boolean success = isAddition ?
                luckPermsHandler.addRole(targetPlayer, cleanedRoleName) :
                luckPermsHandler.removeRole(player, roleName);

        if (success) {
            luckPermsHandler.syncPlayerData(targetPlayer);
            String actionText = isAddition ? "granted" : "removed";
            String preposition = isAddition ? "to" : "from";
            targetPlayer.sendMessage("§aYou have been granted the role: '" + cleanedRoleName + "'.");
            player.sendMessage("§aSuccessfully " + actionText + " role: " + cleanedRoleName + " " + preposition + " " + targetPlayer.getName());
            openMenu(player, isAddition ? MenuLevel.ROLE_ADD_MENU : MenuLevel.HELD_ROLES_MENU, targetPlayer);
        } else {
            player.sendMessage("§cFailed to " + (isAddition ? "add" : "remove") + " role: " + cleanedRoleName);
        }
    }

    private void handleGameModeChange(Player player, ItemStack clickedItem, Player targetPlayer) {
        if (targetPlayer == null) return;

        GameMode newGameMode = null;
        String modeName = "";

        switch (clickedItem.getType()) {
            case GOLDEN_APPLE -> {
                newGameMode = GameMode.CREATIVE;
                modeName = "Creative Mode";
            }
            case APPLE -> {
                newGameMode = GameMode.SURVIVAL;
                modeName = "Survival Mode";
            }
            case ENDER_EYE -> {
                newGameMode = GameMode.SPECTATOR;
                modeName = "Spectator Mode";
            }
            case GRASS_BLOCK -> {
                newGameMode = GameMode.ADVENTURE;
                modeName = "Adventure Mode";
            }
        }

        if (newGameMode != null) {
            targetPlayer.setGameMode(newGameMode);
            sendGameModeChangeMessages(player, targetPlayer, modeName);
        }
    }

    private void sendGameModeChangeMessages(Player sender, Player target, String modeName) {
        sender.sendMessage("§6You granted " + modeName + " to " + target.getName() + "!");
        target.sendMessage("§6You have been granted " + modeName + "!");
    }

    private void handlePlayerHeadClick(Player player, MenuType menuType, ItemStack clickedItem) {
        ItemMeta meta = clickedItem.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) {
            return;
        }

        if (skullMeta.getOwningPlayer() == null) {
            return;
        }

        String targetPlayerName = skullMeta.getOwningPlayer().getName();
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage("§cError: Could not find player " + targetPlayerName);
            return;
        }

        // Store the sender and target player pair in the map
        MenuNavigator.targetPlayers.put(player.getUniqueId(), targetPlayer.getUniqueId());

        // Navigate to appropriate menu
        MenuLevel nextMenu = (menuType != MenuType.PLAYER_OPTIONS) ?
                MenuLevel.PLAYER_OPTIONS : MenuLevel.HELD_ROLES_MENU;
        openMenu(player, nextMenu, targetPlayer);
    }

    private void openMenu(Player player, MenuLevel menuLevel, Player targetPlayer) {
        if (targetPlayer == null) {
            player.sendMessage("§cError: Could not identify target player.");
            return;
        }
        MenuNavigator.openMenu(player, menuLevel, targetPlayer);
    }
}
