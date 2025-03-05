package com.belws.unluckygui.listeners;

import com.belws.unluckygui.menus.ConfirmationMenu;
import com.belws.unluckygui.utils.MenuNavigator;
import com.belws.unluckygui.utils.MenuLevel;
import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryListener implements Listener {

    private final LuckPermsHandler luckPermsHandler;

    public InventoryListener(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        event.setCancelled(true); // Prevent inventory interactions

        // Get the title of the inventory being clicked
        String inventoryTitle = event.getView().title().toString();

        // Handle menu navigation and button actions based on the menu type
        if (event.getInventory().getHolder() instanceof MenuHolder menuHolder) {
            MenuType menuType = menuHolder.getMenuType();

            // Handle actions for the Main Menu
            if (menuType == MenuType.MAIN_MENU) {
                if (clickedItem.getType() == Material.BOOK) {
                    // Navigate to the list menu
                    MenuNavigator.openMenu(player, MenuLevel.LIST_MENU, player);
                }
            }

            // Handle actions for the Held Roles Menu
            if (menuType == MenuType.HELD_ROLES_MENU) {
                if (clickedItem.getType() == Material.PAPER) {
                    // Handle role removal (when clicking a role in the held roles menu)
                    ItemMeta meta = clickedItem.getItemMeta();
                    if (meta != null && meta.displayName() != null) {
                        Component roleName = meta.displayName();
                        Player targetPlayer = MenuNavigator.getTargetPlayer(player);

                        // Open confirmation menu for removing the role
                        if (targetPlayer != null) {
                            ConfirmationMenu.openMenu(
                                    player,
                                    "Are you sure you want to remove the role: " + roleName + "?",
                                    () -> {
                                        // Remove the role from the target player
                                        luckPermsHandler.removeRole(targetPlayer, roleName.toString());
                                        player.sendMessage("The role " + roleName + " was successfully removed from " + targetPlayer.getName());
                                    },
                                    () -> player.sendMessage("Role removal has been canceled.")
                            );
                        }
                    }
                }
            }

            // Handle Creative Mode button in the Options menu
            if (inventoryTitle.contains("Options for") && clickedItem.getType() == Material.GOLDEN_APPLE) {
                Player targetPlayer = MenuNavigator.getTargetPlayer(player); // Get the target player
                if (targetPlayer != null) {
                    targetPlayer.setGameMode(org.bukkit.GameMode.CREATIVE);
                    player.sendMessage("§6You granted Creative Mode to " + targetPlayer.getName() + "!");
                    targetPlayer.sendMessage("§6You have been granted Creative Mode!");
                }
            }

            // Handle Go Back button in any menu
            if (clickedItem.getType() == Material.BARRIER && clickedItem.hasItemMeta()) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.displayName() != null &&
                        meta.displayName().equals(Component.text("Go Back", NamedTextColor.RED))) {
                    // Go back to the previous menu
                    MenuNavigator.goBack(player);
                }
            }

            // Handle Player Head click (open PlayerOptions for the correct player)
            if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.hasItemMeta()) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta instanceof SkullMeta skullMeta && skullMeta.getOwningPlayer() != null) {
                    Player targetPlayer = event.getWhoClicked().getServer().getPlayer(skullMeta.getOwningPlayer().getName());
                    if (targetPlayer != null) {
                        MenuNavigator.openMenu(player, MenuLevel.PLAYER_OPTIONS, targetPlayer);
                    }
                }
            }
        }
    }
}
