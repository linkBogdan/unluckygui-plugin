package com.belws.unluckygui.menus;

import com.belws.unluckygui.core.PluginMain;
import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmationMenu {

    private final Runnable confirmAction;
    private final Runnable cancelAction;

    public ConfirmationMenu(Runnable confirmAction, Runnable cancelAction) {
        this.confirmAction = confirmAction;
        this.cancelAction = cancelAction;
    }

    
    public static void openMenu(Player player, String actionMessage, Runnable confirmAction, Runnable cancelAction) {
        ConfirmationMenu confirmationMenu = new ConfirmationMenu(confirmAction, cancelAction);

        Inventory inventory = PluginMain.getInstance().getServer().createInventory(
                new MenuHolder(MenuType.CONFIRMATION_MENU), 9, Component.text(actionMessage)
        );

        ItemStack confirmItem = createItem(Material.GREEN_WOOL, "Confirm", NamedTextColor.GREEN);
        
        ItemStack cancelItem = createItem(Material.RED_WOOL, "Cancel", NamedTextColor.RED);
        inventory.setItem(3, confirmItem);
        inventory.setItem(5, cancelItem);

        player.openInventory(inventory);
    }

    
    public void handleConfirmation(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        
        if (slot == 3) {
            confirmAction.run();
            player.sendMessage("§6Action confirmed!");
        }
        
        else if (slot == 5) {
            cancelAction.run();
            player.sendMessage("§cAction canceled.");
        }

        
        player.closeInventory();
    }

    
    private static ItemStack createItem(Material material, String name, NamedTextColor color) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name, color));
            item.setItemMeta(meta);
        }
        return item;
    }
}
