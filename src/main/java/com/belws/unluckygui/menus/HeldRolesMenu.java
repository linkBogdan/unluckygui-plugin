package com.belws.unluckygui.menus;

import com.belws.unluckygui.luckperms.LuckPermsHandler;
import com.belws.unluckygui.utils.MenuType;
import com.belws.unluckygui.utils.SlotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                meta.displayName(Component.text(role, NamedTextColor.GRAY));
                meta.lore(List.of(
                        Component.text(context, NamedTextColor.YELLOW),
                        Component.text(expiration, NamedTextColor.RED)
                ));

                roleItem.setItemMeta(meta);
                inventory.setItem(i, roleItem);
            }
        });

        sender.openInventory(inventory);
    }
}
