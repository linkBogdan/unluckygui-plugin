package com.belws.unluckygui.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class LuckPermsHandler {
    private final LuckPerms luckPerms;

    public LuckPermsHandler() {
        this.luckPerms = LuckPermsProvider.get();
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
     * Remove a role from the player.
     */
    public boolean removeRole(Player player, String roleName) {
        User user = getUser(player.getUniqueId());
        if (user == null) return false;

        Optional<Node> nodeToRemove = user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .filter(node -> ((InheritanceNode) node).getGroupName().equalsIgnoreCase(roleName))
                .findFirst();

        if (nodeToRemove.isPresent()) {
            user.data().remove(nodeToRemove.get());
            luckPerms.getUserManager().saveUser(user);
            return true;
        }

        return false;
    }

    /**
     * Helper method to fetch a LuckPerms User object.
     */
    private User getUser(UUID uuid) {
        return luckPerms.getUserManager().getUser(uuid);
    }
}
