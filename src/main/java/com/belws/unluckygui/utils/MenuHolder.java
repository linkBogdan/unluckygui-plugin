package com.belws.unluckygui.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.entity.Player;

public class MenuHolder implements InventoryHolder {
    private final MenuType menuType;
    private final Runnable confirmAction;
    private final Runnable cancelAction;
    private Player targetPlayer;

    public MenuHolder(MenuType menuType, Runnable confirmAction, Runnable cancelAction) {
        this.menuType = menuType;
        this.confirmAction = confirmAction;
        this.cancelAction = cancelAction;
    }

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

    /**
     SOON
     */

    public Runnable getConfirmAction() {
        return confirmAction;
    }

    public Runnable getCancelAction() {
        return cancelAction;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

}
