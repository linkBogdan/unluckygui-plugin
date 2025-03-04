package com.belws.unluckygui.menus;

import com.belws.unluckygui.utils.SlotManager;
import com.belws.unluckygui.utils.PlayerListManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ListMenu {

    private static final int CONTENT_SLOTS = 45;  // The first 45 slots are for content

    public static Inventory createMenu(Player player) {
        // Create the menu using SlotManager
        Inventory inventory = SlotManager.createMenu(CONTENT_SLOTS, Component.text("List Menu"));

        // Get the list of player heads and add them to the inventory
        List<ItemStack> playerHeads = PlayerListManager.generatePlayerHeads(player);


        // Iterate through the player heads and add them to the content slots
        int slot = 0;
        for (ItemStack playerHead : playerHeads) {
            // We won't exceed the CONTENT_SLOTS, so just stop adding if there are too many players
            if (slot >= CONTENT_SLOTS) break;
            inventory.setItem(slot++, playerHead);
        }

        return inventory;
    }
}
