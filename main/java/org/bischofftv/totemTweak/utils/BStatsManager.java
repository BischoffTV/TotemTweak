package org.bischofftv.totemTweak.utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BStatsManager {
    private final Plugin plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private Object metrics;
    private boolean bStatsAvailable = false;

    public BStatsManager(Plugin plugin, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        checkBStatsAvailability();
        initializeBStats();
    }

    private void checkBStatsAvailability() {
        try {
            // Try the relocated bStats class first
            Class.forName("org.bischofftv.totemTweak.libs.bstats.Metrics");
            bStatsAvailable = true;
        } catch (ClassNotFoundException e1) {
            try {
                // Fallback to original bStats class
                Class.forName("org.bstats.bukkit.Metrics");
                bStatsAvailable = true;
            } catch (ClassNotFoundException e2) {
                bStatsAvailable = false;
                plugin.getLogger().info("bStats not found in classpath - metrics will be disabled.");
            }
        }
    }

    private void initializeBStats() {
        if (!configManager.isBStatsEnabled()) {
            messageManager.sendConsoleMessage("bstats-disabled");
            return;
        }

        if (!bStatsAvailable) {
            plugin.getLogger().info("bStats is not available - metrics disabled.");
            return;
        }

        try {
            plugin.getLogger().info("Initializing bStats metrics...");
            
            // Try to initialize bStats with plugin ID 26799
            Class<?> metricsClass = null;
            
            // Try the relocated class first
            try {
                metricsClass = Class.forName("org.bischofftv.totemTweak.libs.bstats.Metrics");
            } catch (ClassNotFoundException e) {
                // Fallback to original class
                metricsClass = Class.forName("org.bstats.bukkit.Metrics");
            }
            
            // Use the correct constructor that expects JavaPlugin
            if (plugin instanceof JavaPlugin) {
                metrics = metricsClass.getConstructor(JavaPlugin.class, int.class).newInstance((JavaPlugin) plugin, 26799);
            } else {
                plugin.getLogger().warning("Plugin is not a JavaPlugin instance - bStats initialization skipped.");
                return;
            }
            
            plugin.getLogger().info("bStats metrics initialized successfully with plugin ID 26799");
            messageManager.sendConsoleMessage("bstats-enabled");
        } catch (Throwable e) {
            plugin.getLogger().warning("Failed to initialize bStats: " + e.getMessage());
            e.printStackTrace();
            metrics = null;
        }
    }

    public void reloadBStats() {
        // Disable current metrics if they exist
        if (metrics != null) {
            metrics = null;
        }
        
        // Reinitialize with new config
        initializeBStats();
    }

    public boolean isEnabled() {
        return metrics != null;
    }

    public boolean isBStatsAvailable() {
        return bStatsAvailable;
    }
} 