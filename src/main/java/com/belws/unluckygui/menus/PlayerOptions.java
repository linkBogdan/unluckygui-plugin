package com.belws.unluckygui.menus;

import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.utils.SlotManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerOptions {

    private static final int CONTENT_SLOTS = 45;

    public static Inventory createMenu(Player target) {
        // Use Component instead of String for the title to avoid deprecation
        Component title = Component.text("Options for : " + target.getName());
        Inventory inventory = Bukkit.createInventory(
                new MenuHolder(MenuType.PLAYER_OPTIONS), // Pass the menu type here
                CONTENT_SLOTS + 9,  // Total slots including navigation row
                title // Use Component for the title
        );

        // Populate navigation buttons (Go Back, etc.)
        SlotManager.populateNavigation(inventory);

        // Create the "Grant Creative Mode" item
        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta appleMeta = goldenApple.getItemMeta();
        appleMeta.displayName(Component.text("Grant Creative Mode", NamedTextColor.GOLD));
        goldenApple.setItemMeta(appleMeta);

        inventory.setItem(22, goldenApple);

        // Create the player head item
        ItemStack playerRoles = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta rolesMeta = playerRoles.getItemMeta();
        if (rolesMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(target);
            skullMeta.displayName(Component.text("Owned roles for " + target.getName(), NamedTextColor.AQUA));
            playerRoles.setItemMeta(skullMeta);
        }

        inventory.setItem(23, playerRoles);

        return inventory;
    }
}
