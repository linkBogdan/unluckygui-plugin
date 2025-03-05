package com.belws.unluckygui.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;

public class MainMenu {

    public static Inventory createMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuType.MAIN_MENU), 27, Component.text("Unlucky Menu"));

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta headMeta = playerHead.getItemMeta();
        if (headMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            skullMeta.displayName(Component.text(player.getName(), NamedTextColor.AQUA));
            playerHead.setItemMeta(skullMeta);
        }

        inventory.setItem(12, playerHead);

        
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        if (bookMeta != null) {
            bookMeta.displayName(Component.text("Player List", NamedTextColor.GRAY)); 
            book.setItemMeta(bookMeta); 
        }

        inventory.setItem(14, book);  


        return inventory; 
    }
}
