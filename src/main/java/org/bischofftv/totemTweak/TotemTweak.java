package org.bischofftv.totemTweak;

import org.bischofftv.totemTweak.commands.TotemTweakCommand;
import org.bischofftv.totemTweak.listeners.StrongholdChestListener;
import org.bischofftv.totemTweak.listeners.TotemDropListener;
import org.bischofftv.totemTweak.utils.BStatsManager;
import org.bischofftv.totemTweak.utils.ConfigManager;
import org.bischofftv.totemTweak.utils.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TotemTweak extends JavaPlugin {
    private ConfigManager configManager;
    private MessageManager messageManager;
    private BStatsManager bStatsManager;

    @Override
    public void onEnable() {
        // Initialize managers
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(configManager);
        bStatsManager = new BStatsManager(this, configManager, messageManager);

        // Register listeners
        getServer().getPluginManager().registerEvents(new TotemDropListener(this, configManager, messageManager), this);
        getServer().getPluginManager().registerEvents(new StrongholdChestListener(this, configManager, messageManager), this);

        // Register commands
        TotemTweakCommand command = new TotemTweakCommand(this, messageManager);
        if (getCommand("totemtweak") != null) {
            getCommand("totemtweak").setExecutor(command);
            getCommand("totemtweak").setTabCompleter(command);
        } else {
            getLogger().severe("Failed to register TotemTweak command! Check plugin.yml configuration.");
        }

        // Send startup message
        messageManager.sendConsoleMessage("enabled");
        
        getLogger().info("TotemTweak has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        messageManager.sendConsoleMessage("disabled");
        getLogger().info("TotemTweak has been disabled!");
    }

    public void reloadPlugin() {
        // Reload configurations
        configManager.reloadConfigs();
        
        // Reinitialize message manager with new config
        messageManager = new MessageManager(configManager);
        
        // Reload bStats with new config
        bStatsManager.reloadBStats();
        
        getLogger().info("TotemTweak configuration reloaded!");
    }
}
