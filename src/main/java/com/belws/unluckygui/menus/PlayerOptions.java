package com.belws.unluckygui.menus;

import com.belws.unluckygui.utils.SlotManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerOptions {

    private static final int CONTENT_SLOTS = 45;

    public static Inventory createMenu(Player target) {
        String title = "Options for : " + target.getName();
        Inventory inventory = SlotManager.createMenu(CONTENT_SLOTS, Component.text(title));

        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta appleMeta = goldenApple.getItemMeta();
        appleMeta.displayName(Component.text("Grant Creative Mode", NamedTextColor.GOLD));
        goldenApple.setItemMeta(appleMeta);

        inventory.setItem(22, goldenApple);

        return inventory;
    }
}
