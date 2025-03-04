package com.belws.unluckygui.listeners;

import com.belws.unluckygui.menus.ListMenu;
import com.belws.unluckygui.menus.MainMenu;
import com.belws.unluckygui.menus.PlayerOptions;
import com.belws.unluckygui.utils.MenuNavigator;
import com.belws.unluckygui.utils.MenuLevel;
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        String inventoryTitle = event.getView().title().toString();
        event.setCancelled(true); // Prevent inventory interactions

        // Navigate menus
        if (inventoryTitle.contains("Unlucky Menu") && clickedItem.getType() == Material.BOOK) {
            MenuNavigator.openMenu(player, MenuLevel.LIST_MENU, player);
        }

        if (clickedItem.getType() == Material.GOLDEN_APPLE && inventoryTitle.contains("Options for")) {
            Player targetPlayer = MenuNavigator.getTargetPlayer(player); // Get the actual target

            if (targetPlayer != null) {
                targetPlayer.setGameMode(org.bukkit.GameMode.CREATIVE);
                player.sendMessage("§6You granted Creative Mode to " + targetPlayer.getName() + "!");
                targetPlayer.sendMessage("§6You have been granted Creative Mode!");
            }
        }

        // Handle "Go Back" button
        if (clickedItem.getType() == Material.BARRIER && clickedItem.hasItemMeta()) {
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta != null && meta.displayName() != null &&
                    meta.displayName().equals(Component.text("Go Back", NamedTextColor.RED))) {
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
