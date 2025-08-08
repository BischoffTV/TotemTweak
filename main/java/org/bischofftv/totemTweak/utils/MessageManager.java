package org.bischofftv.totemTweak.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageManager {
    private final ConfigManager configManager;

    public MessageManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void sendMessage(CommandSender sender, String path) {
        sendMessage(sender, path, null);
    }

    public void sendMessage(CommandSender sender, String path, String[] placeholders) {
        FileConfiguration messages = configManager.getMessages();
        String message = messages.getString(path, "Message not found: " + path);
        
        if (placeholders != null) {
            for (int i = 0; i < placeholders.length; i += 2) {
                if (i + 1 < placeholders.length) {
                    message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
                }
            }
        }
        
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void sendConsoleMessage(String path) {
        sendConsoleMessage(path, null);
    }

    public void sendConsoleMessage(String path, String[] placeholders) {
        FileConfiguration messages = configManager.getMessages();
        String message = messages.getString("console." + path, "Console message not found: " + path);
        
        if (placeholders != null) {
            for (int i = 0; i < placeholders.length; i += 2) {
                if (i + 1 < placeholders.length) {
                    message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
                }
            }
        }
        
        System.out.println(ChatColor.translateAlternateColorCodes('&', message));
    }

    public String getMessage(String path) {
        FileConfiguration messages = configManager.getMessages();
        String message = messages.getString(path, "Message not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
} 