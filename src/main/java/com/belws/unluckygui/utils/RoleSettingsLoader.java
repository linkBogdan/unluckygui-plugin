package com.belws.unluckygui.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoleSettingsLoader {

    private static File file;
    private static FileConfiguration config;
    private static Set<String> blacklistedRoles;

    /**
     * Loads or creates roleSettings.yml in plugins/UnluckyGUI/.
     */
    public static void loadSettings() {
        File pluginFolder = new File("plugins/UnluckyGUI");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs(); // Create folder if missing
        }

        file = new File(pluginFolder, "roleSettings.yml"); // Changed to match your capitalization

        if (!file.exists()) {
            copyDefaultConfig(); // Copy from JAR resources if missing
        }

        config = YamlConfiguration.loadConfiguration(file);

        // Loading the set of blacklisted roles as a list
        if (config.contains("blacklist") && config.isList("blacklist")) {
            List<String> roles = config.getStringList("blacklist");
            blacklistedRoles = new HashSet<>(roles);
        } else {
            blacklistedRoles = new HashSet<>();
        }
    }

    /**
     * Checks if a role is blacklisted.
     */
    public static boolean isRoleBlacklisted(String role) {
        return blacklistedRoles != null && blacklistedRoles.contains(role);
    }

    /**
     * Copies the default roleSettings.yml from the JAR to plugins/UnluckyGUI/.
     */
    private static void copyDefaultConfig() {
        try (InputStream input = RoleSettingsLoader.class.getClassLoader().getResourceAsStream("roleSettings.yml")) {
            if (input == null) {
                Bukkit.getLogger().warning("[UnluckyGUI] Could not find roleSettings.yml in resources!");
                return;
            }
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[UnluckyGUI] Failed to copy roleSettings.yml: " + e.getMessage());
        }
    }

    public static Set<String> getBlacklistedRoles() {
        return blacklistedRoles != null ? blacklistedRoles : new HashSet<>();
    }
}