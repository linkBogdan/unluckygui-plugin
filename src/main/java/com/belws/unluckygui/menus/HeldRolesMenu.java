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

        List<String> targetRoles = luckPermsHandler.getPlayerRoles(target);

        Inventory inventory = Bukkit.createInventory(
                new MenuHolder(MenuType.HELD_ROLES_MENU),
                27,
                Component.text("Roles owned by: " + target.getName()) 
        );

        
        for (int i = 0; i < targetRoles.size(); i++) {
            String role = targetRoles.get(i);

            
            ItemStack roleItem = new ItemStack(Material.PAPER);
            ItemMeta meta = roleItem.getItemMeta();
            if (meta != null) {
                
                meta.displayName(Component.text(role, NamedTextColor.GRAY));

                roleItem.setItemMeta(meta);
                inventory.setItem(i, roleItem);
            }
        }

        sender.openInventory(inventory);
    }
}
