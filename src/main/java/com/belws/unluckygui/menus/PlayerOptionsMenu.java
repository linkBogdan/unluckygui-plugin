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

public class PlayerOptionsMenu {

    private static final int CONTENT_SLOTS = 45;

    public static Inventory createMenu(Player viewer, Player target) {
        Component title = Component.text("Options for: " + target.getName());

        Inventory inventory = Bukkit.createInventory(
                new MenuHolder(MenuType.PLAYER_OPTIONS),
                CONTENT_SLOTS + 9,
                title
        );

        SlotManager.populateNavigation(inventory);

        ItemStack creativeModeButton = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta creativeModeMeta = creativeModeButton.getItemMeta();
        if (creativeModeMeta != null) {
            creativeModeMeta.displayName(Component.text("Grant Creative Mode", NamedTextColor.GOLD));
            creativeModeButton.setItemMeta(creativeModeMeta);
        }

        inventory.setItem(2, creativeModeButton);

        ItemStack survivalModeButton = new ItemStack(Material.APPLE);
        ItemMeta survivalModeMeta = survivalModeButton.getItemMeta();
        if (survivalModeMeta != null) {
            survivalModeMeta.displayName(Component.text("Grant Survival Mode", NamedTextColor.GOLD));
            survivalModeButton.setItemMeta(survivalModeMeta);
        }
        inventory.setItem(1, survivalModeButton);

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta rolesMeta = playerHead.getItemMeta();
        if (rolesMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(target);
            skullMeta.displayName(Component.text("&7Owned roles for " + target.getName(), NamedTextColor.AQUA));
            playerHead.setItemMeta(skullMeta);
        }

        inventory.setItem(4, playerHead);


        return inventory;
    }
}
