package com.belws.unluckygui.menus;

import com.belws.unluckygui.luckperms.LuckPermsHandler;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.utils.RoleNameFormatter;
import com.belws.unluckygui.utils.SlotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.stream.IntStream;

public class HeldRolesMenu {

    private final LuckPermsHandler luckPermsHandler;

    public HeldRolesMenu(LuckPermsHandler luckPermsHandler) {
        this.luckPermsHandler = luckPermsHandler;
    }

    private static final int CONTENT_SLOTS = 18;

    /**
     * Opens the Role Selection menu for the given player but targets the roles of the other player (target).
     */
    public void openMenu(Player sender, Player target) {
        List<String> targetRoles = luckPermsHandler.getPlayerRoles(target);
        List<String> roleContexts = luckPermsHandler.getPlayerRolesWithContext(target);
        List<String> roleExpirations = luckPermsHandler.getPlayerRolesWithExpiration(target);

        Component title = Component.text("Roles owned by: " + target.getName());
        Inventory inventory = SlotManager.createMenu(CONTENT_SLOTS, title, MenuType.HELD_ROLES_MENU);

        IntStream.range(0, targetRoles.size()).forEach(i -> {
            String role = targetRoles.get(i);
            String context = i < roleContexts.size() ? roleContexts.get(i) : "Context: global";
            String expiration = i < roleExpirations.size() ? roleExpirations.get(i) : "Expires: permanent";

            ItemStack roleItem = new ItemStack(Material.PAPER);
            ItemMeta meta = roleItem.getItemMeta();
            if (meta != null) {
                // Format role name using RoleNameFormatter
                String formattedRole = RoleNameFormatter.formatRoleName(role);

                // Set the display name of the item
                meta.displayName(Component.text(formattedRole, NamedTextColor.GRAY));

                // Add context and expiration to the lore
                meta.lore(List.of(
                        Component.text(context, NamedTextColor.YELLOW),
                        Component.text(expiration, NamedTextColor.RED)
                ));

                // Store the raw role name in persistent data for later removal
                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(new NamespacedKey("unluckygui", "role"), PersistentDataType.STRING, role);

                roleItem.setItemMeta(meta);
                inventory.setItem(i, roleItem);
            }
        });

        sender.openInventory(inventory);
    }

    /**
     * Retrieves the raw role from the clicked item.
     */
    public String getRawRoleFromItem(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("unluckygui", "role");
                if (container.has(key, PersistentDataType.STRING)) {
                    String rawRole = container.get(key, PersistentDataType.STRING);
                    return rawRole;
                }
            }
        }
        return null; // Return null if no rawRole is found
    }

    /**
     * Handles role removal when a player clicks on a held role item.
     */
    public void handleRoleRemoval(Player player, ItemStack clickedItem, Player targetPlayer) {
        String rawRole = getRawRoleFromItem(clickedItem);
        if (rawRole == null) {
            player.sendMessage("§cError: Could not find the raw role.");
            return;
        }

        if (rawRole.equalsIgnoreCase("default")) {
            player.sendMessage("§cError: You cannot remove the 'default' role.");
            return;
        }

        // Remove the raw role from the target player
        boolean success = luckPermsHandler.removeRole(targetPlayer, "group." + rawRole);
        if (success) {
            luckPermsHandler.syncPlayerData(targetPlayer);
            player.sendMessage("§aSuccessfully removed role: " + rawRole + " from " + targetPlayer.getName());
            targetPlayer.sendMessage("§aYour role '" + rawRole + "' has been removed.");
            openMenu(player, targetPlayer);  // Refresh the held roles menu
        } else {
            player.sendMessage("HRM:§cFailed to remove role: " + rawRole);
        }
    }
}
