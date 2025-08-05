package org.bischofftv.totemTweak.commands;

import org.bischofftv.totemTweak.TotemTweak;
import org.bischofftv.totemTweak.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TotemTweakCommand implements CommandExecutor, TabCompleter {
    private final TotemTweak plugin;
    private final MessageManager messageManager;

    public TotemTweakCommand(TotemTweak plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("totemtweak.reload")) {
                    messageManager.sendMessage(sender, "plugin.no-permission");
                    return true;
                }
                reloadPlugin(sender);
                break;
            case "help":
                showHelp(sender);
                break;
            default:
                messageManager.sendMessage(sender, "plugin.help.title");
                messageManager.sendMessage(sender, "plugin.help.help");
                messageManager.sendMessage(sender, "plugin.help.reload");
                break;
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        messageManager.sendMessage(sender, "plugin.help.title");
        messageManager.sendMessage(sender, "plugin.help.help");
        messageManager.sendMessage(sender, "plugin.help.reload");
    }

    private void reloadPlugin(CommandSender sender) {
        try {
            plugin.reloadPlugin();
            messageManager.sendMessage(sender, "plugin.reload.success");
            messageManager.sendConsoleMessage("reload");
        } catch (Exception e) {
            messageManager.sendMessage(sender, "plugin.reload.error");
            plugin.getLogger().severe("Error reloading plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subcommands = Arrays.asList("reload", "help");
            for (String subcommand : subcommands) {
                if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subcommand);
                }
            }
        }

        return completions;
    }
} 