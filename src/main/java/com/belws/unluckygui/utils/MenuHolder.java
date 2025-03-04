package com.belws.unluckygui.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuHolder implements InventoryHolder {
    private MenuType menuType;  // Change from String to MenuType
    private String previousMenuName;

    public MenuHolder(MenuType menuType) {
        this.menuType = menuType;
        this.previousMenuName = null;
    }

    public MenuHolder(MenuType menuType, String previousMenuName) {
        this.menuType = menuType;
        this.previousMenuName = previousMenuName;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public String getPreviousMenuName() {
        return previousMenuName;
    }
}
