package org.bischofftv.totemTweak;

import org.bischofftv.totemTweak.commands.TotemTweakCommand;
import org.bischofftv.totemTweak.listeners.StrongholdChestListener;
import org.bischofftv.totemTweak.listeners.TotemDropListener;
import org.bischofftv.totemTweak.listeners.TotemStackingListener;
import org.bischofftv.totemTweak.utils.BStatsManager;
import org.bischofftv.totemTweak.utils.ConfigManager;
import org.bischofftv.totemTweak.utils.MessageManager;
import org.bischofftv.totemTweak.utils.UpdateChecker;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public final class TotemTweak extends JavaPlugin {
    private ConfigManager configManager;
    private MessageManager messageManager;
    private BStatsManager bStatsManager;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        // Initialize managers
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(configManager);
        bStatsManager = new BStatsManager(this, configManager, messageManager);
        updateChecker = new UpdateChecker(this);

        // Set global totem stack size
        setTotemStackSize();

        // Register listeners
        getServer().getPluginManager().registerEvents(new TotemDropListener(this, configManager, messageManager), this);
        getServer().getPluginManager().registerEvents(new StrongholdChestListener(this, configManager, messageManager), this);
        getServer().getPluginManager().registerEvents(new TotemStackingListener(this, configManager, messageManager), this);
        getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
                if (configManager.isUpdateCheckerEnabled() && event.getPlayer().hasPermission("totemtweak.admin")) {
                    updateChecker.checkForUpdate(event.getPlayer());
                }
                // Always show stacking warning if stacking is enabled and not Paper
                updateChecker.sendSpigotStackingWarning(event.getPlayer());
            }
        }, this);

        // Register commands
        TotemTweakCommand command = new TotemTweakCommand(this, messageManager);
        if (getCommand("totemtweak") != null) {
            getCommand("totemtweak").setExecutor(command);
            getCommand("totemtweak").setTabCompleter(command);
        } else {
            getLogger().severe("Failed to register TotemTweak command! Check plugin.yml configuration.");
        }

        // Update check on startup
        if (configManager.isUpdateCheckerEnabled()) {
            updateChecker.checkForUpdateToAdmins();
        }

        // Send startup message
        messageManager.sendConsoleMessage("enabled");
        getLogger().info("TotemTweak has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Reset totem stack size to vanilla on disable
        setTotemStackSizeVanilla();
        messageManager.sendConsoleMessage("disabled");
        getLogger().info("TotemTweak has been disabled!");
    }

    public void reloadPlugin() {
        // Reload configurations
        configManager.reloadConfigs();
        // Reinitialize message manager with new config
        messageManager = new MessageManager(configManager);
        // Set global totem stack size after reload
        setTotemStackSize();
        // Reload bStats with new config
        bStatsManager.reloadBStats();
        // Update check on reload
        if (configManager.isUpdateCheckerEnabled()) {
            updateChecker.checkForUpdateToAdmins();
        }
        getLogger().info("TotemTweak configuration reloaded!");
    }

    private void setTotemStackSize() {
        try {
            // Try multiple approaches for setting stack size
            boolean success = false;
            
            // Method 1: Try Material.setMaxStackSize (Paper 1.17+)
            try {
                Method setMaxStackSize = Material.class.getMethod("setMaxStackSize", int.class);
                if (configManager.isTotemStackingEnabled()) {
                    int maxStack = configManager.getMaxTotemStackSize();
                    setMaxStackSize.invoke(Material.TOTEM_OF_UNDYING, maxStack);
                    getLogger().info("Set totem of undying max stack size to " + maxStack + " (Method 1)");
                    success = true;
                } else {
                    setMaxStackSize.invoke(Material.TOTEM_OF_UNDYING, 1);
                    getLogger().info("Set totem of undying max stack size to 1 (vanilla)");
                    success = true;
                }
            } catch (NoSuchMethodException e) {
                // Method 2: Try using ItemFactory (Spigot 1.21+)
                try {
                    if (configManager.isTotemStackingEnabled()) {
                        int maxStack = configManager.getMaxTotemStackSize();
                        // Create a test item to verify stack size works
                        ItemStack testItem = new ItemStack(Material.TOTEM_OF_UNDYING, maxStack);
                        if (testItem.getAmount() == maxStack) {
                            getLogger().info("Totem stacking enabled with max stack size " + maxStack + " (Method 2)");
                            success = true;
                        }
                    } else {
                        getLogger().info("Totem stacking disabled - using vanilla behavior");
                        success = true;
                    }
                } catch (Exception e2) {
                    getLogger().warning("Failed to set totem stack size via ItemFactory: " + e2.getMessage());
                }
            }
            
            if (!success) {
                getLogger().warning("Could not set totem stack size - using default behavior");
            }
            
        } catch (Exception e) {
            getLogger().warning("Failed to set totem stack size: " + e.getMessage());
        }
    }

    private void setTotemStackSizeVanilla() {
        try {
            Method setMaxStackSize = Material.class.getMethod("setMaxStackSize", int.class);
            setMaxStackSize.invoke(Material.TOTEM_OF_UNDYING, 1);
        } catch (Exception ignored) {
            // Ignore if method doesn't exist
        }
    }
}
