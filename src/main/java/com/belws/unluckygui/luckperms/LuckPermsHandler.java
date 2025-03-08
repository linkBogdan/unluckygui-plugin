package com.belws.unluckygui.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
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

        System.out.println("Removing role " + roleName + " from target player: " + targetPlayer.getName());


        User user = getUser(targetPlayer.getUniqueId());
        if (user == null) return false;

        String fullRoleName = roleName.startsWith("group.") ? roleName.substring(6) : roleName;
        
        System.out.println("[UnluckyGUI] " + targetPlayer.getName() + " current roles: " + user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> ((InheritanceNode) node).getGroupName())
                .collect(Collectors.joining(", ")));

        
        boolean hasRole = user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> ((InheritanceNode) node).getGroupName())
                .anyMatch(role -> role.equalsIgnoreCase(fullRoleName));

        if (!hasRole) {
            System.out.println("[UnluckyGUI] ERROR: " + targetPlayer.getName() + " does not have the role " + fullRoleName);
            return false; 
        }
        
        InheritanceNode nodeToRemove = InheritanceNode.builder(fullRoleName).build();
        if (user.data().remove(nodeToRemove).wasSuccessful()) {
            
            if (user.getPrimaryGroup().equalsIgnoreCase(fullRoleName)) {
                user.setPrimaryGroup("default");
            }

            luckPerms.getUserManager().saveUser(user); 
            luckPerms.getUserManager().loadUser(targetPlayer.getUniqueId()); 

            return true;
        }

        return false;
    }
    public void syncPlayerData(Player player) {
        
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            
            luckPerms.getUserManager().saveUser(user); 
        }
    }

    /**
     * Helper method to fetch a LuckPerms User object.
     */
    private User getUser(UUID uuid) {
        return luckPerms.getUserManager().getUser(uuid);
    }
}
