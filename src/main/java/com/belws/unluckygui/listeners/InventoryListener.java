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

public class InventoryListener implements Listener {

    private final LuckPermsHandler luckPermsHandler;
    private final Map<Player, Player> playerTargetMap; // Store sender-target pairs

    public InventoryListener(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
        this.playerTargetMap = new HashMap<>(); // Initialize the map
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        event.setCancelled(true);

        if (!(event.getInventory().getHolder() instanceof MenuHolder menuHolder)) return;
        MenuType menuType = menuHolder.getMenuType();

        // Retrieve the target player from the stored map
        Player targetPlayer = playerTargetMap.get(player);

        // Main Menu Interaction
        if (menuType == MenuType.MAIN_MENU && clickedItem.getType() == Material.BOOK) {
            openMenu(player, MenuLevel.LIST_MENU, player);
        }

        // Held Roles Menu Interaction
        if (menuType == MenuType.HELD_ROLES_MENU && clickedItem.getType() == Material.PAPER) {
            handleRoleRemoval(player, clickedItem, targetPlayer);
        }

        // Game Mode Interaction (Player Options)
        if (menuType == MenuType.PLAYER_OPTIONS) {
            handleGameModeChange(player, clickedItem, targetPlayer);
        }

        // Go Back Interaction
        if (clickedItem.getType() == Material.BARRIER && clickedItem.hasItemMeta()) {
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta != null && meta.displayName() != null &&
                    meta.displayName().equals(Component.text("Go Back", NamedTextColor.RED))) {
                MenuNavigator.goBack(player);
            }
        }

        // Player Head (Navigation)
        if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.hasItemMeta()) {
            handlePlayerHeadClick(player, menuType, clickedItem);
        }
    }

    // Handle Role Removal in Held Roles Menu
    private void handleRoleRemoval(Player player, ItemStack clickedItem, Player targetPlayer) {
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || meta.displayName() == null || targetPlayer == null) return;

        Component roleNameComponent = meta.displayName();
        String cleanedRoleName = PlainTextComponentSerializer.plainText().serialize(roleNameComponent).trim();
        String fullRoleName = "group." + cleanedRoleName;

        if (cleanedRoleName.equalsIgnoreCase("default")) return;

        boolean success = luckPermsHandler.removeRole(targetPlayer, fullRoleName);
        if (success) {
            luckPermsHandler.syncPlayerData(targetPlayer);
            targetPlayer.sendMessage("§aYour role '" + cleanedRoleName + "' has been removed.");
            player.sendMessage("§aSuccessfully removed role: " + cleanedRoleName + " from " + targetPlayer.getName());
        } else {
            player.sendMessage("§cFailed to remove role: " + cleanedRoleName);
        }
    }

    // Handle Game Mode Changes in Player Options Menu
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
            player.sendMessage("§6You granted " + modeName + " to " + targetPlayer.getName() + "!");
            targetPlayer.sendMessage("§6You have been granted " + modeName + "!");
        }
    }

    // Handle Player Head Clicks for Navigation
    private void handlePlayerHeadClick(Player player, MenuType menuType, ItemStack clickedItem) {
        ItemMeta meta = clickedItem.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta) || skullMeta.getOwningPlayer() == null) return;

        Player targetPlayer = player.getServer().getPlayer(skullMeta.getOwningPlayer().getName());
        if (targetPlayer == null) return;

        // Store the sender and target player pair in the map
        playerTargetMap.put(player, targetPlayer);

        if (menuType != MenuType.PLAYER_OPTIONS) {
            openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
        } else {
            openMenu(player, MenuLevel.HELD_ROLES_MENU, targetPlayer);
        }
    }

    // Helper method to open menus
    private void openMenu(Player player, MenuLevel menuLevel, Player targetPlayer) {
        MenuNavigator menuNavigator = new MenuNavigator(luckPermsHandler);
        menuNavigator.openMenu(player, menuLevel, targetPlayer);
    }
}
