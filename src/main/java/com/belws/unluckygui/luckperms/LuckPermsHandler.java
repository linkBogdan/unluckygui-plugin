package com.belws.unluckygui.luckperms;

import com.belws.unluckygui.core.PluginMain;
import com.belws.unluckygui.utils.ContextUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
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
     * Fetch all available roles (groups) from LuckPerms.
     */
    public List<String> getAllRoles() {
        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .map(Group::getName)
                .collect(Collectors.toList());
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

        // Remove "group." prefix if it's already there
        String groupName = roleName.startsWith("group.") ? roleName.substring(6) : roleName;

        // Check if the group actually exists in LuckPerms
        if (!luckPerms.getGroupManager().isLoaded(groupName)) return false;

        try {
            InheritanceNode node = InheritanceNode.builder(groupName).build();
            user.data().add(node);
            luckPerms.getUserManager().saveUser(user);

            // Sync changes to ensure they take effect immediately
            syncPlayerData(player);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Remove a role from the target player fetched from MenuNavigator.
     */
    public boolean removeRole(Player viewer, String rawRoleName) {
        Player targetPlayer = getTargetPlayer(viewer);

        if (targetPlayer == null) {
            viewer.sendMessage("§cError: No target player found.");
            return false;
        }

        String fullRoleName = rawRoleName.startsWith("group.") ? rawRoleName.substring(6) : rawRoleName;

        User user = getUser(targetPlayer.getUniqueId());
        if (user == null) return false;

        // Get the server context for the role
        String serverContext = getPlayerCurrentServer(targetPlayer, fullRoleName);

        // Fetch the nodes of the player
        List<InheritanceNode> allNodes = user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> (InheritanceNode) node)
                .collect(Collectors.toList());

        // Find the nodes to remove
        List<InheritanceNode> nodesToRemove = allNodes.stream()
                .filter(node -> node.getGroupName().equalsIgnoreCase(fullRoleName))
                .filter(node -> {
                    String nodeContext = node.getContexts().getAnyValue("server").orElse("global");
                    return nodeContext.equalsIgnoreCase(serverContext);
                })
                .collect(Collectors.toList());

        if (nodesToRemove.isEmpty()) {
            viewer.sendMessage("§cError: " + targetPlayer.getName() + " does not have the role " + fullRoleName + " on server " + serverContext);
            return false;
        }

        // Attempt to remove the nodes
        boolean removed = false;
        for (InheritanceNode node : nodesToRemove) {
            if (user.data().remove(node).wasSuccessful()) {
                removed = true;
            }
        }

        if (!removed) return false;

        // Save and reload the user data
        try {
            luckPerms.getUserManager().saveUser(user);
            luckPerms.getUserManager().loadUser(targetPlayer.getUniqueId());
        } catch (Exception e) {
            viewer.sendMessage("§cError: Failed to save changes.");
            return false;
        }

        return true;
    }

    /**
     * Sync player data to ensure LuckPerms updates are applied properly.
     */
    public void syncPlayerData(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {});
            luckPerms.getUserManager().saveUser(user);
            luckPerms.getUserManager().loadUser(player.getUniqueId());

            player.recalculatePermissions();

            Bukkit.getScheduler().runTaskLater(PluginMain.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " parent info");
                player.recalculatePermissions();
            }, 20L);
        }
    }

    /**
     * Get the player's current server context for a specific role.
     */
    private String getPlayerCurrentServer(Player player, String roleName) {
        User user = getUser(player.getUniqueId());
        if (user == null) return "global";

        return user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> (InheritanceNode) node)
                .filter(node -> node.getGroupName().equalsIgnoreCase(roleName))
                .flatMap(node -> node.getContexts().getAnyValue("server").stream())
                .findFirst()
                .orElse("global");
    }

    /**
     * Helper method to fetch a LuckPerms User object.
     */
    private User getUser(UUID uuid) {
        return luckPerms.getUserManager().getUser(uuid);
    }

    /**
     * Get the target player for the viewer (including self-targeting).
     */
    public static Player getTargetPlayer(Player viewer) {
        UUID targetUUID = MenuNavigator.targetPlayers.get(viewer.getUniqueId()); // Get the target based on the viewer's UUID
        if (targetUUID == null) {
            // If no target is set, it means the viewer is interacting with themselves
            MenuNavigator.targetPlayers.put(viewer.getUniqueId(), viewer.getUniqueId()); // Set the viewer as the target
            return viewer;  // Return the viewer as the target (for self-modification)
        }
        Player targetPlayer = Bukkit.getPlayer(targetUUID); // Retrieve the player based on UUID
        return targetPlayer; // Return the actual target player
    }
}
