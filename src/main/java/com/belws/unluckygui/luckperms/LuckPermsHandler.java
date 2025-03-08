package com.belws.unluckygui.luckperms;

import com.belws.unluckygui.core.PluginMain;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.belws.unluckygui.utils.MenuNavigator; 

public class LuckPermsHandler {
    private final LuckPerms luckPerms;

    public LuckPermsHandler() {
        this.luckPerms = LuckPermsProvider.get();
    }

    /**
     * Fetch a list of groups assigned to the player along with their context (e.g., server).
     */
    public List<String> getPlayerRolesWithContext(Player player) {
        User user = getUser(player.getUniqueId());
        if (user == null) return List.of();

        return user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> {
                    InheritanceNode inheritanceNode = (InheritanceNode) node;
                    String group = inheritanceNode.getGroupName();
                    String serverContext = inheritanceNode.getContexts().getAnyValue("server").orElse("global");
                    return group + " (Server: " + serverContext + ")";
                })
                .collect(Collectors.toList());
    }

    /**
     * Fetch a list of groups assigned to the player along with expiration time.
     */
    public List<String> getPlayerRolesWithExpiration(Player player) {
        User user = getUser(player.getUniqueId());
        if (user == null) return List.of();

        return user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> {
                    InheritanceNode inheritanceNode = (InheritanceNode) node;
                    String group = inheritanceNode.getGroupName();
                    String expiration = inheritanceNode.hasExpiry() ? inheritanceNode.getExpiry().toString() : "permanent";
                    return group + " (Expires: " + expiration + ")";
                })
                .collect(Collectors.toList());
    }
    /**
     * Fetch a list of groups assigned to the player.
     */
    public List<String> getPlayerRoles(Player player) {
        User user = getUser(player.getUniqueId());
        if (user == null) return List.of();

        return user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> ((InheritanceNode) node).getGroupName())
                .collect(Collectors.toList());
    }

    /**
     * Check if the player has a specific role.
     */
    public boolean hasRole(Player player, String roleName) {
        User user = getUser(player.getUniqueId());
        if (user == null) return false;

        return user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> ((InheritanceNode) node).getGroupName())
                .anyMatch(role -> role.equalsIgnoreCase(roleName));
    }

    /**
     * Add a role to the player.
     */
    public boolean addRole(Player player, String roleName) {
        User user = getUser(player.getUniqueId());
        if (user == null) return false;

        InheritanceNode node = InheritanceNode.builder(roleName).build();
        user.data().add(node);
        luckPerms.getUserManager().saveUser(user);
        return true;
    }

    /**
     * Remove a role from the target player fetched from MenuNavigator.
     */
    public boolean removeRole(Player viewer, String roleName) {
        Player targetPlayer = MenuNavigator.getTargetPlayer(viewer);
        if (targetPlayer == null) {
            viewer.sendMessage("§cError: No target player found.");
            return false;
        }

        System.out.println("Attempting to remove role " + roleName + " from target player: " + targetPlayer.getName());

        User user = getUser(targetPlayer.getUniqueId());
        if (user == null) return false;

        String fullRoleName = roleName.startsWith("group.") ? roleName.substring(6) : roleName;

        // Get player's current server (assuming there's a method to get the player's current server)
        String serverContext = getPlayerCurrentServer(targetPlayer, fullRoleName); // Implement this method

        // Find only nodes that match the specific role AND context
        List<InheritanceNode> nodesToRemove = user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> (InheritanceNode) node)
                .filter(node -> node.getGroupName().equalsIgnoreCase(fullRoleName))
                .filter(node -> {
                    // Check if the node has a server context and matches the current server
                    return node.getContexts().getAnyValue("server")
                            .map(ctx -> ctx.equalsIgnoreCase(serverContext)) // Only remove if it matches the server
                            .orElse(true); // If there's no server context, it’s global
                })
                .collect(Collectors.toList());

        if (nodesToRemove.isEmpty()) {
            System.out.println("[UnluckyGUI] ERROR: " + targetPlayer.getName() + " does not have the role " + fullRoleName + " on server " + serverContext);
            return false;
        }

        // Remove only relevant nodes
        boolean removed = false;
        for (InheritanceNode node : nodesToRemove) {
            if (user.data().remove(node).wasSuccessful()) {
                System.out.println("[UnluckyGUI] Removed role: " + fullRoleName + " (Context: " + node.getContexts() + ")");
                removed = true;
            }
        }

        if (!removed) {
            System.out.println("[UnluckyGUI] Failed to remove role: " + fullRoleName);
            return false;
        }

        // Ensure changes are applied and persisted
        luckPerms.getUserManager().saveUser(user);
        luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {});
        luckPerms.getUserManager().loadUser(targetPlayer.getUniqueId());

        return true;
    }

    public void syncPlayerData(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            // Ensure user changes are saved and reloaded
            luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {});
            luckPerms.getUserManager().saveUser(user);
            luckPerms.getUserManager().loadUser(player.getUniqueId());

            // Clear and refresh the player's permissions
            player.recalculatePermissions();

            // Wait a tick and force LuckPerms to refresh by running a console command
            Bukkit.getScheduler().runTaskLater(PluginMain.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " parent info");
            }, 20L); // Delay by 1 second (20 ticks) to allow LuckPerms to process changes
        }
    }






    private String getPlayerCurrentServer(Player player, String roleName) {
        User user = getUser(player.getUniqueId());
        if (user == null) return "global";

        return user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> (InheritanceNode) node)
                .filter(node -> node.getGroupName().equalsIgnoreCase(roleName))
                .flatMap(node -> node.getContexts().getAnyValue("server").stream()) // Extract server context if it exists
                .findFirst() // Get the first match
                .orElse("global"); // Default to global if no specific server is found
    }




    /**
     * Helper method to fetch a LuckPerms User object.
     */
    private User getUser(UUID uuid) {
        return luckPerms.getUserManager().getUser(uuid);
    }
}
