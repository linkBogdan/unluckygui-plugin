package com.belws.unluckygui.menus;

import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.utils.SlotManager;
import com.belws.unluckygui.utils.PlayerListManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ListMenu {

    private static final int CONTENT_SLOTS = 45;  

    public static Inventory createMenu(Player player) {

        Component title = Component.text("List", NamedTextColor.AQUA);
        Inventory inventory = SlotManager.createMenu(CONTENT_SLOTS, title, MenuType.LIST_MENU);

        
        List<ItemStack> playerHeads = PlayerListManager.generatePlayerHeads(player);


        
        int slot = 0;
        for (ItemStack playerHead : playerHeads) {
            
            if (slot >= CONTENT_SLOTS) break;
            inventory.setItem(slot++, playerHead);
        }

        return inventory;
    }
}
