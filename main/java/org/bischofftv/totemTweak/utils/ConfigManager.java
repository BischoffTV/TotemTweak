package org.bischofftv.totemTweak.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

public class ConfigManager {
    private final Plugin plugin;
    private FileConfiguration config;
    private File configFile;
    private FileConfiguration messages;
    private File messagesFile;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        // Load main config
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        updateConfigWithDefaults();

        // Load messages config
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * Updates the config with missing default values (does not overwrite existing values)
     */
    private void updateConfigWithDefaults() {
        InputStream defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream == null) return;
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
        boolean changed = mergeDefaults(config, defConfig, "");
        if (changed) {
            try {
                config.options().copyDefaults(true);
                config.save(new File(plugin.getDataFolder(), "config.yml"));
                plugin.getLogger().info("Updated config.yml with missing default values.");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to update config.yml: " + e.getMessage());
            }
        }
    }

    /**
     * Recursively merges missing keys from defaults into the config
     */
    private boolean mergeDefaults(FileConfiguration config, ConfigurationSection defaults, String path) {
        boolean changed = false;
        Set<String> keys = defaults.getKeys(false);
        for (String key : keys) {
            String fullPath = path.isEmpty() ? key : path + "." + key;
            if (!config.contains(fullPath)) {
                config.set(fullPath, defaults.get(key));
                changed = true;
            } else if (defaults.isConfigurationSection(key)) {
                ConfigurationSection section = config.getConfigurationSection(fullPath);
                if (section == null) {
                    config.createSection(fullPath);
                    section = config.getConfigurationSection(fullPath);
                }
                changed |= mergeDefaults(config, defaults.getConfigurationSection(key), fullPath);
            }
        }
        return changed;
    }

    public void reloadConfigs() {
        loadConfigs();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public double getTotemDropChance() {
        return config.getDouble("totem-drop-chance", 0.3);
    }

    public boolean isPreventOtherDrops() {
        return config.getBoolean("prevent-other-drops", true);
    }

    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }

    public boolean isBStatsEnabled() {
        return config.getBoolean("bstats-enabled", true);
    }

    // Update checker
    public boolean isUpdateCheckerEnabled() {
        return config.getBoolean("update-checker.enabled", true);
    }

    // Totem stacking methods
    public boolean isTotemStackingEnabled() {
        return config.getBoolean("totem-stacking.enabled", false);
    }

    public int getMaxTotemStackSize() {
        int maxSize = config.getInt("totem-stacking.max-stack-size", 16);
        // Ensure the value is within valid bounds (1-64)
        return Math.max(1, Math.min(64, maxSize));
    }

    // Individual drop prevention methods
    public boolean isEntityDeathPrevented() {
        return config.getBoolean("drop-prevention.entity-death", true);
    }

    public boolean isEntityDropPrevented() {
        return config.getBoolean("drop-prevention.entity-drop", true);
    }

    public boolean isPlayerDeathPrevented() {
        return config.getBoolean("drop-prevention.player-death", true);
    }

    public boolean isPlayerDropPrevented() {
        return config.getBoolean("drop-prevention.player-drop", true);
    }

    public boolean isMinecartDestructionPrevented() {
        return config.getBoolean("drop-prevention.minecart-destruction", true);
    }

    public boolean isChestDestructionPrevented() {
        return config.getBoolean("drop-prevention.chest-destruction", true);
    }

    public boolean isHopperDestructionPrevented() {
        return config.getBoolean("drop-prevention.hopper-destruction", true);
    }

    public boolean isDispenserDestructionPrevented() {
        return config.getBoolean("drop-prevention.dispenser-destruction", true);
    }

    public boolean isDropperDestructionPrevented() {
        return config.getBoolean("drop-prevention.dropper-destruction", true);
    }

    public boolean isBarrelDestructionPrevented() {
        return config.getBoolean("drop-prevention.barrel-destruction", true);
    }

    public boolean isBlockBreakPrevented() {
        return config.getBoolean("drop-prevention.block-break", true);
    }

    public boolean isExplosionPrevented() {
        return config.getBoolean("drop-prevention.explosion", true);
    }

    public boolean isPistonPrevented() {
        return config.getBoolean("drop-prevention.piston", true);
    }

    // Generic method to check if a specific drop type is prevented
    public boolean isDropPrevented(String dropType) {
        return config.getBoolean("drop-prevention." + dropType, true);
    }

    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save messages.yml: " + e.getMessage());
        }
    }
} 