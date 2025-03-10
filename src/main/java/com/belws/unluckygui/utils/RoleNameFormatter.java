package com.belws.unluckygui.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RoleNameFormatter {

    private static final Map<String, String> ROLE_DISPLAY_NAMES = new HashMap<>();
    private static File file;
    private static FileConfiguration config;

    /**
     * Loads or creates roleFormat.yml in plugins/UnluckyGUI/.
     */
    public static void loadRoles() {
        File pluginFolder = new File("plugins/UnluckyGUI");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs(); // Create folder if missing
        }

        file = new File(pluginFolder, "roleFormat.yml");

        if (!file.exists()) {
            copyDefaultConfig(); // Copy from JAR resources if missing
        }

        config = YamlConfiguration.loadConfiguration(file);
        ROLE_DISPLAY_NAMES.clear();

        if (config.contains("roles")) {
            config.getConfigurationSection("roles").getKeys(false).forEach(role -> {
                String displayName = config.getString("roles." + role);
                ROLE_DISPLAY_NAMES.put(role.toLowerCase(), displayName);
            });
        }
    }

    /**
     * Formats a role name using the YAML config. Falls back on auto-formatting.
     */
    public static String formatRoleName(String role) {
        return ROLE_DISPLAY_NAMES.getOrDefault(role.toLowerCase(), autoFormatRoleName(role));
    }

    /**
     * Automatically formats unknown roles (e.g., "super_admin" -> "Super Admin").
     */
    public static String autoFormatRoleName(String role) {
        return Arrays.stream(role.split("(?=[A-Z])|_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    /**
     * Copies the default roleFormat.yml from the JAR to plugins/UnluckyGUI/.
     */
    private static void copyDefaultConfig() {
        try (InputStream input = RoleNameFormatter.class.getClassLoader().getResourceAsStream("roleFormat.yml")) {
            if (input == null) {
                Bukkit.getLogger().warning("[UnluckyGUI] Could not find roleFormat.yml in resources!");
                return;
            }
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[UnluckyGUI] Failed to copy roleFormat.yml: " + e.getMessage());
        }
    }
}
