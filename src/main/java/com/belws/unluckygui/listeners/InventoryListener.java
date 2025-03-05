package com.belws.unluckygui.listeners;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
//import com.belws.unluckygui.menus.ConfirmationMenu;
import com.belws.unluckygui.utils.MenuNavigator;
import com.belws.unluckygui.utils.MenuLevel;
import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.luckperms.LuckPermsHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;

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

        event.setCancelled(true); 

        
        String inventoryTitle = event.getView().title().toString();

        
        if (event.getInventory().getHolder() instanceof MenuHolder menuHolder) {
            MenuType menuType = menuHolder.getMenuType();

            
            if (menuType == MenuType.MAIN_MENU) {
                if (clickedItem.getType() == Material.BOOK) {
                    
                    MenuNavigator.openMenu(player, MenuLevel.LIST_MENU, player);
                }
            }

            
            if (menuType == MenuType.HELD_ROLES_MENU) {
                if (clickedItem.getType() == Material.PAPER) {
                    
                    ItemMeta meta = clickedItem.getItemMeta();
                    if (meta != null && meta.displayName() != null) {
                        Component roleNameComponent = meta.displayName();

                        
                        String cleanedRoleName = PlainTextComponentSerializer.plainText().serialize(roleNameComponent).trim();

                        
                        String fullRoleName = "group." + cleanedRoleName;

                        
                        Player targetPlayer = MenuNavigator.getTargetPlayer(player); 

                        if (targetPlayer != null) {
                            
                            String command = "lp user " + targetPlayer.getName() + " permission unset " + fullRoleName;

                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);

                        } else {
                            player.sendMessage("§cNo target player specified.");
                        }
                    }
                }
            }
            
            if (inventoryTitle.contains("Options for") && clickedItem.getType() == Material.GOLDEN_APPLE) {
                Player targetPlayer = MenuNavigator.getTargetPlayer(player); 
                if (targetPlayer != null) {
                    targetPlayer.setGameMode(org.bukkit.GameMode.CREATIVE);
                    player.sendMessage("§6You granted Creative Mode to " + targetPlayer.getName() + "!");
                    targetPlayer.sendMessage("§6You have been granted Creative Mode!");
                }
            }

            
            if (clickedItem.getType() == Material.BARRIER && clickedItem.hasItemMeta()) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.displayName() != null &&
                        meta.displayName().equals(Component.text("Go Back", NamedTextColor.RED))) {
                    
                    MenuNavigator.goBack(player);
                }
            }

            
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
