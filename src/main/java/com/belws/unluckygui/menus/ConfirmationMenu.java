package com.belws.unluckygui.menus;

import com.belws.unluckygui.core.PluginMain;
import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmationMenu {
    private final Component actionMessage;
    private final Runnable confirmAction;
    private final Runnable cancelAction;

    public ConfirmationMenu(
            String actionMessage,
            Runnable confirmAction,
            Runnable cancelAction
    ) {
        this.actionMessage = Component.text(actionMessage);
        this.confirmAction = confirmAction;
        this.cancelAction = cancelAction;
    }

    public ConfirmationMenu(
            String actionMessage,
            Runnable confirmAction
    ) {
        this(actionMessage, confirmAction, () -> {});
    }

    public void openMenu(Player player) {
        Inventory inventory = PluginMain.getInstance().getServer().createInventory(
                new MenuHolder(MenuType.CONFIRMATION_MENU),
                9,
                actionMessage
        );
        // Confirm item
        ItemStack confirmItem = createItem(Material.GREEN_WOOL, "Confirm", NamedTextColor.GREEN);
        // Cancel item
        ItemStack cancelItem = createItem(Material.RED_WOOL, "Cancel", NamedTextColor.RED);

        inventory.setItem(3, confirmItem);
        inventory.setItem(5, cancelItem);

        player.openInventory(inventory);
    }

    private ItemStack createItem(Material material, String name, NamedTextColor color) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name, color));
            item.setItemMeta(meta);
        }
        return item;
    }

    public void handleConfirmation(Player player, int slot) {
        if (slot == 3) {
            // Confirm action
            confirmAction.run();
        } else if (slot == 5) {
            // Cancel action
            cancelAction.run();
        }
    }
}