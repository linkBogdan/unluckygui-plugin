package com.belws.unluckygui.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class PlayerListManager {

    /**
     * Generates a list of player heads with their names for the ListMenu, excluding the player themselves.
     * This will set the player's skin to the head.
     * @param viewer The player who is viewing the menu.
     * @return A list of items (player heads) to be used in the menu.
     */
    public static List<ItemStack> generatePlayerHeads(Player viewer) {
        List<ItemStack> playerHeads = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (onlinePlayer.equals(viewer)) {
                continue;
            }

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta headMeta = playerHead.getItemMeta();

            if (headMeta instanceof SkullMeta skullMeta) {
                skullMeta.displayName(Component.text(onlinePlayer.getName(), NamedTextColor.GREEN));

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Click to interact", NamedTextColor.GRAY));
                skullMeta.lore(lore);

                skullMeta.setOwningPlayer(onlinePlayer);

                skullMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                playerHead.setItemMeta(skullMeta);
            }

            playerHeads.add(playerHead);
        }

        return playerHeads;
    }
}
