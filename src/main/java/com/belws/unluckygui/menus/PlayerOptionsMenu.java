package com.belws.unluckygui.menus;

import com.belws.unluckygui.utils.MenuHolder;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.utils.SlotManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.List;

public class PlayerOptionsMenu {

    private static final int CONTENT_SLOTS = 18; // Set the desired content slots

    public static Inventory createMenu(Player viewer, Player target) {
        // Title for the menu
        Component title = Component.text("Options for: " + target.getName());

        // Create the menu with 27 content slots, the correct menu type, and title
        Inventory inventory = SlotManager.createMenu(CONTENT_SLOTS, title, MenuType.PLAYER_OPTIONS);
        
        // Game mode buttons
        ItemStack creativeModeButton = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta creativeModeMeta = creativeModeButton.getItemMeta();
        if (creativeModeMeta != null) {
            creativeModeMeta.displayName(Component.text("Grant Creative Mode", NamedTextColor.GOLD));
            creativeModeButton.setItemMeta(creativeModeMeta);
        }
        inventory.setItem(2, creativeModeButton);

        ItemStack survivalModeButton = new ItemStack(Material.APPLE);
        ItemMeta survivalModeMeta = survivalModeButton.getItemMeta();
        if (survivalModeMeta != null) {
            survivalModeMeta.displayName(Component.text("Grant Survival Mode", NamedTextColor.GOLD));
            survivalModeButton.setItemMeta(survivalModeMeta);
        }
        inventory.setItem(1, survivalModeButton);

        ItemStack spectatorModeButton = new ItemStack(Material.ENDER_EYE);
        ItemMeta spectatorModeMeta = spectatorModeButton.getItemMeta();
        if (spectatorModeMeta != null) {
            spectatorModeMeta.displayName(Component.text("Grant Spectator Mode", NamedTextColor.GOLD));
            spectatorModeButton.setItemMeta(spectatorModeMeta);
        }
        inventory.setItem(6, spectatorModeButton);

        ItemStack adventureModeButton = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta adventureModeMeta = adventureModeButton.getItemMeta();
        if (adventureModeMeta != null) {
            adventureModeMeta.displayName(Component.text("Grant Adventure Mode", NamedTextColor.GOLD));
            adventureModeButton.setItemMeta(adventureModeMeta);
        }
        inventory.setItem(7, adventureModeButton);

        // View roles button (Diamond)
        ItemStack viewRolesButton = new ItemStack(Material.DIAMOND);
        ItemMeta viewRolesMeta = viewRolesButton.getItemMeta();
        if (viewRolesMeta != null) {
            viewRolesMeta.displayName(Component.text("View Roles", NamedTextColor.AQUA));
            viewRolesMeta.lore(List.of(
                Component.text("Click to view roles for ", NamedTextColor.GRAY)
                    .append(Component.text(target.getName(), NamedTextColor.YELLOW))
            ));
            viewRolesButton.setItemMeta(viewRolesMeta);
        }
        inventory.setItem(4, viewRolesButton);

        // Add role button (Emerald)
        ItemStack addRoleButton = new ItemStack(Material.EMERALD);
        ItemMeta addRoleMeta = addRoleButton.getItemMeta();
        if (addRoleMeta != null) {
            addRoleMeta.displayName(Component.text("Add Role", NamedTextColor.GREEN));
            addRoleMeta.lore(List.of(
                Component.text("Click to add a new role to ", NamedTextColor.GRAY)
                    .append(Component.text(target.getName(), NamedTextColor.YELLOW))
            ));
            addRoleButton.setItemMeta(addRoleMeta);
        }
        inventory.setItem(13, addRoleButton);

        return inventory;
    }
}
