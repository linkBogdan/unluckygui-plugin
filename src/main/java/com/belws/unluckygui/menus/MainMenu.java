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
        // Create the inventory with 27 slots
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuType.MAIN_MENU), 27, Component.text("Unlucky Menu"));

        // Create player head item
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta headMeta = playerHead.getItemMeta();
        if (headMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player); // Use the Player object directly
            skullMeta.displayName(Component.text(player.getName(), NamedTextColor.AQUA));
            playerHead.setItemMeta(skullMeta);
        }

        inventory.setItem(12, playerHead);  // Set player head at position 11

        // Create placeholder book item
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        if (bookMeta != null) {
            bookMeta.displayName(Component.text("Placeholder Name", NamedTextColor.GRAY));
            book.setItemMeta(bookMeta);
        }

        inventory.setItem(14, book);  // Set book at position 13

        return inventory;
    }
}
