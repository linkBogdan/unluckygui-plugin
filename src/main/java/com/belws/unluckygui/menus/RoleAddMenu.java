package com.belws.unluckygui.menus;

import com.belws.unluckygui.luckperms.LuckPermsHandler;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.utils.RoleNameFormatter;
import com.belws.unluckygui.utils.RoleSettingsLoader;
import com.belws.unluckygui.utils.SlotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoleAddMenu {

    private final LuckPermsHandler luckPermsHandler;
    private final Player targetPlayer;

    public RoleAddMenu(LuckPermsHandler luckPermsHandler, Player targetPlayer) {
        this.luckPermsHandler = luckPermsHandler;
        this.targetPlayer = targetPlayer;
    }

    private static final int CONTENT_SLOTS = 18;

    /**
     * Opens the Role Add menu for the given player, showing all roles except the ones they already have.
     */
    public void openMenu(Player sender, Player target) {
        List<String> allRoles = luckPermsHandler.getAllRoles();
        List<String> ownedRoles = luckPermsHandler.getPlayerRoles(target);

        List<String> availableRoles = allRoles.stream()
                .filter(role -> !ownedRoles.contains(role) && !RoleSettingsLoader.getBlacklistedRoles().contains(role))
                .collect(Collectors.toList());

        Component title = Component.text("Add Roles to: " + target.getName());
        Inventory inventory = SlotManager.createMenu(CONTENT_SLOTS, title, MenuType.ROLE_ADD_MENU);

        IntStream.range(0, Math.min(availableRoles.size(), CONTENT_SLOTS - 1)).forEach(i -> {
            String rawRole = availableRoles.get(i);
            String displayName = RoleNameFormatter.formatRoleName(rawRole);

            ItemStack roleItem = new ItemStack(Material.PAPER);
            ItemMeta meta = roleItem.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(displayName, NamedTextColor.GREEN));
                meta.lore(List.of(
                        Component.text("Click to add this role", NamedTextColor.YELLOW)
                ));

                // Store the raw role name in persistent data for later use
                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(new NamespacedKey("unluckygui", "role"), PersistentDataType.STRING, rawRole);

                roleItem.setItemMeta(meta);
                inventory.setItem(i, roleItem);
            }
        });

        sender.openInventory(inventory);
    }

    // Get the raw role from an item
    public String getRawRoleFromItem(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("unluckygui", "role");
                if (container.has(key, PersistentDataType.STRING)) {
                    return container.get(key, PersistentDataType.STRING);
                }
            }
        }
        return null;
    }

}
