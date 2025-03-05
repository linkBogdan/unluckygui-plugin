
package com.belws.unluckygui.menus;

import com.belws.unluckygui.core.PluginMain;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.function.Consumer;

public class ConfirmationMenu {

    private final String actionMessage;
    private final Consumer<Boolean> onConfirm; // Callback for confirmation action

    public ConfirmationMenu(String actionMessage, Consumer<Boolean> onConfirm) {
        this.actionMessage = actionMessage;
        this.onConfirm = onConfirm;
    }

    /**
     * Opens the confirmation menu for the sender (player)
     */
    public void openMenu(Player player) {
        // Create the inventory with 9 slots (simple confirmation menu)
        Inventory inventory = PluginMain.getInstance().getServer().createInventory(null, 9, Component.text("Confirm Action"));

        // Create green wool (confirm)
        ItemStack greenWool = new ItemStack(Material.GREEN_WOOL);
        ItemMeta greenMeta = greenWool.getItemMeta();
        if (greenMeta != null) {
            greenMeta.displayName(Component.text("Confirm", NamedTextColor.GREEN));
            greenWool.setItemMeta(greenMeta);
        }

        // Create red wool (cancel)
        ItemStack redWool = new ItemStack(Material.RED_WOOL);
        ItemMeta redMeta = redWool.getItemMeta();
        if (redMeta != null) {
            redMeta.displayName(Component.text("Cancel", NamedTextColor.RED));
            redWool.setItemMeta(redMeta);
        }

        // Place the wool in the slots
        inventory.setItem(3, greenWool); // Slot 3 = Confirm
        inventory.setItem(5, redWool);   // Slot 5 = Cancel

        // Open the inventory for the player
        player.openInventory(inventory);
    }

    /**
     * Handle the action based on the item clicked in the confirmation menu
     * @param player the player who clicked the item
     * @param slot the slot number of the item clicked
     */
    public void handleConfirmation(Player player, int slot) {
        if (slot == 3) {
            // Green Wool - Confirm
            onConfirm.accept(true);  // Execute the confirmed action
        } else if (slot == 5) {
            // Red Wool - Cancel
            onConfirm.accept(false); // Cancel the action
        }
    }

}
