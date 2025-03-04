package com.belws.unluckygui.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SlotManager {

    // Constants for the navigation row (bottom row)
    private static final int NAVIGATION_ROW_START = 45;  // Start from slot 45 (the first slot of the bottom row)
    private static final int GO_BACK_SLOT = 49;  // Slot 49 will be the "Go Back" button in the middle

    // Method to populate the navigation row (bottom row)
    public static void populateNavigation(Inventory inventory) {
        // Set "Go Back" button at the center of the navigation row (slot 49)
        ItemStack goBackItem = new ItemStack(Material.BARRIER);
        ItemMeta goBackMeta = goBackItem.getItemMeta();
        if (goBackMeta != null) {
            goBackMeta.displayName(Component.text("Go Back", NamedTextColor.RED));  // "Go Back" button text
            goBackItem.setItemMeta(goBackMeta);
        }
        inventory.setItem(GO_BACK_SLOT, goBackItem);  // Set the "Go Back" button in the center of the bottom row

        // Keep other navigation slots (except the center one) empty
        for (int i = NAVIGATION_ROW_START; i < 54; i++) {
            if (i != GO_BACK_SLOT) {
                inventory.setItem(i, new ItemStack(Material.AIR));  // Set other slots as empty
            }
        }
    }

    // Method to create the inventory for the menu
    public static Inventory createMenu(int contentSlots, Component menuName) {
        // Total slots = content slots + navigation row (9 slots reserved for navigation)
        int totalSlots = contentSlots + 9;
        Inventory inventory = org.bukkit.Bukkit.createInventory(
                new MenuHolder(MenuType.LIST_MENU),
                totalSlots,
                menuName
        );

        // Populate the content slots (first 'contentSlots' slots)
        populateContent(inventory, contentSlots);

        // Populate the navigation row (bottom row with the "Go Back" button)
        populateNavigation(inventory);

        return inventory;
    }

    // Method to populate content slots (first 'contentSlots' slots)
    private static void populateContent(Inventory inventory, int contentSlots) {
        for (int i = 0; i < contentSlots; i++) {
            // Leave the content slots empty for now, you can populate them later as needed
            inventory.setItem(i, new ItemStack(Material.AIR));  // Setting them as empty for now
        }
    }
}
