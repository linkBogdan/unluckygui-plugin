package com.belws.unluckygui.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class MenuUtils {

    // Utility method to create a player head item
    public static ItemStack createPlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            skullMeta.displayName(Component.text(player.getName()));
            playerHead.setItemMeta(skullMeta);
        }
        return playerHead;
    }

    // Utility method to create a generic item with a name
    public static ItemStack createItem(Material material, Component name) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.displayName(name);
            item.setItemMeta(itemMeta);
        }
        return item;
    }
}
