package com.belws.unluckygui.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SlotManager {

    private static final int BOTTOM_ROW_SIZE = 9; // The size of the bottom row (always 9)

    // Method to populate the navigation row (reserved for go back button)
    public static void populateNavigation(Inventory inventory, int totalSlots) {
        // Calculate the starting index of the last row
        int lastRowStart = totalSlots - BOTTOM_ROW_SIZE;

        // Calculate the middle slot of the last row
        int goBackSlot = lastRowStart + (BOTTOM_ROW_SIZE / 2);  // Middle slot of the bottom row

        // Create Go Back item
        ItemStack goBackItem = new ItemStack(Material.BARRIER);
        ItemMeta goBackMeta = goBackItem.getItemMeta();
        if (goBackMeta != null) {
            goBackMeta.displayName(Component.text("Go Back", NamedTextColor.RED));
            goBackItem.setItemMeta(goBackMeta);
        }

        // Set the Go Back button in the calculated middle slot of the last row
        inventory.setItem(goBackSlot, goBackItem);

        // Fill the rest of the bottom row with empty slots, excluding the Go Back slot
        for (int i = lastRowStart; i < totalSlots; i++) {
            if (i != goBackSlot) {
                inventory.setItem(i, new ItemStack(Material.AIR)); // Empty slots
            }
        }
    }

    // Method to create a menu dynamically with a specified number of content slots and a specific menu type
    public static Inventory createMenu(int contentSlots, Component menuName, MenuType menuType) {
        // Calculate the total number of slots including the navigation bar
        int totalSlots = contentSlots + BOTTOM_ROW_SIZE;  // 9 for the bottom row with the Go Back button

        // Ensure the total slots do not exceed 54
        totalSlots = Math.min(totalSlots, 54);

        // Ensure totalSlots is at least 9
        totalSlots = Math.max(totalSlots, 9);

        // Create the inventory with dynamic slots and menu type
        Inventory inventory = org.bukkit.Bukkit.createInventory(
                new MenuHolder(menuType),  // Pass the menu type here
                totalSlots,
                menuName
        );

        // Populate the content section (top part)
        populateContent(inventory, contentSlots);

        // Populate the bottom row with the Go Back button in the dynamic middle slot
        populateNavigation(inventory, totalSlots);

        return inventory;
    }

    // Method to populate the content section with empty items
    private static void populateContent(Inventory inventory, int contentSlots) {
        for (int i = 0; i < contentSlots; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR)); // Empty slots for content
        }
    }
}
