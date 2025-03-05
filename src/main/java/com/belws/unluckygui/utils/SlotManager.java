package com.belws.unluckygui.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SlotManager {

    private static final int NAVIGATION_ROW_START = 45;
    private static final int GO_BACK_SLOT = 49;

    public static void populateNavigation(Inventory inventory) {
        ItemStack goBackItem = new ItemStack(Material.BARRIER);
        ItemMeta goBackMeta = goBackItem.getItemMeta();
        if (goBackMeta != null) {
            goBackMeta.displayName(Component.text("Go Back", NamedTextColor.RED));
            goBackItem.setItemMeta(goBackMeta);
        }
        inventory.setItem(GO_BACK_SLOT, goBackItem);

        for (int i = NAVIGATION_ROW_START; i < 54; i++) {
            if (i != GO_BACK_SLOT) {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }
    }

    public static Inventory createMenu(int contentSlots, Component menuName) {
        int totalSlots = contentSlots + 9;
        Inventory inventory = org.bukkit.Bukkit.createInventory(
                new MenuHolder(MenuType.LIST_MENU),
                totalSlots,
                menuName
        );

        populateContent(inventory, contentSlots);

        populateNavigation(inventory);

        return inventory;
    }

    private static void populateContent(Inventory inventory, int contentSlots) {
        for (int i = 0; i < contentSlots; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
    }
}
