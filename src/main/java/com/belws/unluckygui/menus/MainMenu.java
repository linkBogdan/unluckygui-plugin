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
        // Create the inventory with 27 slots for the MainMenu
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuType.MAIN_MENU), 27, Component.text("Unlucky Menu"));

        // Create player head item (Player's head in the inventory)
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta headMeta = playerHead.getItemMeta();
        if (headMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player); // Set the player's head to display
            skullMeta.displayName(Component.text(player.getName(), NamedTextColor.AQUA)); // Display player name as the name of the head
            playerHead.setItemMeta(skullMeta);
        }

        inventory.setItem(12, playerHead);  // Set player head at position 12 (center of the grid)

        // Example of a book item in the menu (replace with actual use-case items)
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        if (bookMeta != null) {
            bookMeta.displayName(Component.text("Player List", NamedTextColor.GRAY)); // Set a name for the book
            book.setItemMeta(bookMeta); // Apply the meta to the book
        }

        inventory.setItem(14, book);  // Set book at position 14 (just right of center)


        return inventory; // Return the complete inventory
    }
}
