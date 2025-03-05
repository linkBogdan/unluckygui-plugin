package com.belws.unluckygui.utils;

public enum MenuLevel {
    MAIN_MENU(1),
    LIST_MENU(2),
    PLAYER_OPTIONS(3),
    CONFIRMATION_MENU(4),
    HELD_ROLES_MENU(5);

    private final int level;

    MenuLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}