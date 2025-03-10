package com.belws.unluckygui.listeners;

import com.belws.unluckygui.core.PluginMain;
import com.belws.unluckygui.menus.RoleAddMenu;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.belws.unluckygui.utils.MenuNavigator;
import com.belws.unluckygui.utils.MenuLevel;
import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class InventoryListener implements Listener {

    private final LuckPermsHandler luckPermsHandler;
    private final MenuNavigator menuNavigator;
    private final RoleAddMenu roleAddMenu;

    public InventoryListener(LuckPermsHandler luckPermsHandler, RoleAddMenu roleAddMenu) {
        this.luckPermsHandler = luckPermsHandler;
        this.menuNavigator = new MenuNavigator(luckPermsHandler);
        this.roleAddMenu = roleAddMenu;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        // Cancel the event to prevent item movement
        event.setCancelled(true);

        // Get the clicked item
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        // Get the target player from the navigator
        Player targetPlayer = MenuNavigator.getTargetPlayer(player);

        // Handle back button (barrier) - only in role management menus
        if (clickedItem.getType() == Material.BARRIER) {
            MenuType menuType = ((MenuHolder) event.getInventory().getHolder()).getMenuType();
            if (menuType == MenuType.ROLE_ADD_MENU || menuType == MenuType.HELD_ROLES_MENU) {
                MenuNavigator.goBack(player);
            } else {
                player.closeInventory();
            }
            return;
        }

        // Get the current menu type from the inventory
        MenuType menuType = ((MenuHolder) event.getInventory().getHolder()).getMenuType();
        if (menuType == null) return;

        // Handle menu-specific actions
        switch (menuType) {
            case MAIN_MENU:
                handleMainMenuClick(player, clickedItem);
                break;
            case LIST_MENU:
                handleListMenuClick(player, clickedItem);
                break;
            case PLAYER_OPTIONS:
                handlePlayerOptionsClick(player, clickedItem, targetPlayer);
                break;
            case HELD_ROLES_MENU:
                handleHeldRolesClick(player, clickedItem, targetPlayer);
                break;
            case ROLE_ADD_MENU:
                handleRoleAddClick(player, clickedItem, targetPlayer);
                break;
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            // Self options - set target as self
            openMenu(player, MenuLevel.PLAYER_OPTIONS, player);
        } else if (clickedItem.getType() == Material.BOOK) {
            // Player list - no target needed
            openMenu(player, MenuLevel.LIST_MENU, null);
        }
    }

    private void handleListMenuClick(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            String playerName = PlainTextComponentSerializer.plainText().serialize(clickedItem.getItemMeta().displayName());
            Player targetPlayer = player.getServer().getPlayer(playerName);
            if (targetPlayer != null) {
                openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
            }
        }
    }

    private void handlePlayerOptionsClick(Player player, ItemStack clickedItem, Player targetPlayer) {
        if (clickedItem.getType() == Material.DIAMOND) {
            // Diamond button - View roles
            openMenu(player, MenuLevel.HELD_ROLES_MENU, targetPlayer);
        } else if (clickedItem.getType() == Material.EMERALD) {
            // Emerald button - Add roles
            openMenu(player, MenuLevel.ROLE_ADD_MENU, targetPlayer);
        } else if (clickedItem.getType() == Material.GOLDEN_APPLE) {
            // Creative mode
            if (targetPlayer != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode creative " + targetPlayer.getName());
                player.sendMessage("§aSet " + targetPlayer.getName() + "'s gamemode to creative");
            }
        } else if (clickedItem.getType() == Material.APPLE) {
            // Survival mode
            if (targetPlayer != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode survival " + targetPlayer.getName());
                player.sendMessage("§aSet " + targetPlayer.getName() + "'s gamemode to survival");
            }
        } else if (clickedItem.getType() == Material.ENDER_EYE) {
            // Spectator mode
            if (targetPlayer != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode spectator " + targetPlayer.getName());
                player.sendMessage("§aSet " + targetPlayer.getName() + "'s gamemode to spectator");
            }
        } else if (clickedItem.getType() == Material.GRASS_BLOCK) {
            // Adventure mode
            if (targetPlayer != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode adventure " + targetPlayer.getName());
                player.sendMessage("§aSet " + targetPlayer.getName() + "'s gamemode to adventure");
            }
        }
    }

    private void handleHeldRolesClick(Player player, ItemStack clickedItem, Player targetPlayer) {
        if (clickedItem.getType() == Material.EMERALD) {
            openMenu(player, MenuLevel.ROLE_ADD_MENU, targetPlayer);
        } else if (clickedItem.getType() == Material.PAPER) {
            handleRoleManagement(player, clickedItem, targetPlayer, false);
        } else if (clickedItem.getType() == Material.BARRIER) {
            openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
        }
    }

    private void handleRoleAddClick(Player player, ItemStack clickedItem, Player targetPlayer) {
        if (clickedItem.getType() == Material.PAPER) {
            handleRoleManagement(player, clickedItem, targetPlayer, true);
        } else if (clickedItem.getType() == Material.BARRIER) {
            openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
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

    private void handleRoleManagement(Player player, ItemStack clickedItem, Player targetPlayer, boolean isAddition) {
        if (targetPlayer == null) {
            player.sendMessage("§cError: Could not identify target player.");
            return;
        }

        // Use getRawRoleFromItem to get the raw role
        String rawRole = roleAddMenu.getRawRoleFromItem(clickedItem);
        if (rawRole == null || (!isAddition && rawRole.equalsIgnoreCase("default"))) return;

        // Check if the group exists in LuckPerms using getAllRoles()
        List<String> allRoles = luckPermsHandler.getAllRoles();  // Get all available roles
        if (!allRoles.contains(rawRole)) {
            player.sendMessage("§cError: The group '" + rawRole + "' does not exist.");
            return;
        }

        // Remove "group." prefix if it's already there for removal
        String fullRoleName = rawRole.startsWith("group.") ? rawRole.substring(6) : rawRole;

        // Add or remove the role based on the action
        boolean success = isAddition
                ? luckPermsHandler.addRole(targetPlayer, fullRoleName)
                : luckPermsHandler.removeRole(targetPlayer, fullRoleName);

        if (success) {
            // First sync the player data
            luckPermsHandler.syncPlayerData(targetPlayer);
            
            // Send initial success message
            player.sendMessage("§aProcessing " + (isAddition ? "role addition" : "role removal") + "...");
            
            // Only apply delay for role changes, not navigation
            Bukkit.getScheduler().runTaskLater(PluginMain.getInstance(), () -> {
                // Double-check if the role change was successful
                boolean hasRole = luckPermsHandler.hasRole(targetPlayer, fullRoleName);
                if ((isAddition && hasRole) || (!isAddition && !hasRole)) {
                    // Role change was successful, send final messages
                    player.sendMessage("§aSuccessfully " + (isAddition ? "granted" : "removed") + " role: " + fullRoleName + " " + (isAddition ? "to" : "from") + " " + targetPlayer.getName());
                    targetPlayer.sendMessage("§aYour role has been updated: '" + fullRoleName + "'.");
                    
                    // Refresh the menu
                    openMenu(player, isAddition ? MenuLevel.ROLE_ADD_MENU : MenuLevel.HELD_ROLES_MENU, targetPlayer);
                } else {
                    // Role change failed or took too long
                    player.sendMessage("§cRole update verification failed. Please try again.");
                }
            }, 40L); // Wait 2 seconds (40 ticks) before refreshing
        } else {
            player.sendMessage("§cFailed to " + (isAddition ? "add" : "remove") + " role: " + fullRoleName);
        }
    }

    private void openMenu(Player player, MenuLevel menuLevel, Player targetPlayer) {
        if (targetPlayer == null) {
            player.sendMessage("§cError: Could not identify target player.");
            return;
        }
        MenuNavigator.openMenu(player, menuLevel, targetPlayer);
    }
}
