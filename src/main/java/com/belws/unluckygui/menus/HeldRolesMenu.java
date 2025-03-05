package com.belws.unluckygui.menus;

import com.belws.unluckygui.luckperms.LuckPermsHandler;
import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class HeldRolesMenu {

    private final LuckPermsHandler luckPermsHandler;

    public HeldRolesMenu(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
    }

    /**
     * Opens the Role Selection menu for the given player but targets the roles of the other player (target).
     */
    public void openMenu(Player sender, Player target) {
        // Get the target player's roles
        List<String> targetRoles = luckPermsHandler.getPlayerRoles(target);

        // Create the menu inventory using the new createInventory method
        Inventory inventory = Bukkit.createInventory(
                new MenuHolder(MenuType.HELD_ROLES_MENU),
                27,
                Component.text("Roles owned by: " + target.getName())
        );

        // Add the roles to the inventory
        for (int i = 0; i < targetRoles.size(); i++) {
            String role = targetRoles.get(i);

            // Create the item for the role
            ItemStack roleItem = new ItemStack(Material.PAPER);
            ItemMeta meta = roleItem.getItemMeta();
            if (meta != null) {
                // Use the new Component API for setting display names
                meta.displayName(Component.text(role, NamedTextColor.GRAY));

                // Add the item to the inventory
                roleItem.setItemMeta(meta);
                inventory.setItem(i, roleItem);
            }
        }

        // Open the inventory for the sender (not the target)
        sender.openInventory(inventory);
    }
}
