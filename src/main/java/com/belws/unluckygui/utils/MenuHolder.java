package com.belws.unluckygui.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuHolder implements InventoryHolder {
    private final MenuType menuType;
    private final Runnable confirmAction;
    private final Runnable cancelAction;

    // Constructor for menus that require confirm/cancel actions
    public MenuHolder(MenuType menuType, Runnable confirmAction, Runnable cancelAction) {
        this.menuType = menuType;
        this.confirmAction = confirmAction;
        this.cancelAction = cancelAction;
    }

    // Constructor for other menus that don't need actions
    public MenuHolder(MenuType menuType) {
        this(menuType, () -> {}, () -> {});
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public Runnable getConfirmAction() {
        return confirmAction;
    }

    public Runnable getCancelAction() {
        return cancelAction;
    }
}
