package org.bischofftv.totemTweak.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private static final int SPIGOT_RESOURCE_ID = 127675;
    private static final String SPIGOT_URL = "https://www.spigotmc.org/resources/totemtweak.127675/";
    private static final String MODRINTH_URL = "https://modrinth.com/plugin/totemtweak";
    private final Plugin plugin;
    private final String currentVersion;
    private String latestVersion;
    private boolean updateAvailable = false;

    public UpdateChecker(Plugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void checkForUpdate(CommandSender notifyTarget) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    latestVersion = fetchLatestSpigotVersion();
                    if (latestVersion != null && !currentVersion.equalsIgnoreCase(latestVersion)) {
                        updateAvailable = true;
                        sendUpdateMessage(notifyTarget);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Update check failed: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void checkForUpdateToAdmins() {
        checkForUpdate(Bukkit.getConsoleSender());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("totemtweak.admin")) {
                checkForUpdate(player);
                sendSpigotStackingWarning(player);
            }
        }
    }

    public void sendSpigotStackingWarning(Player player) {
        if (isSpigotServer() && isTotemStackingEnabled()) {
            player.sendMessage("§c§l[TotemTweak] §r§c❗ Totem stacking is enabled, but this feature does §lNOT§r§c work on Spigot or its forks due to server limitations.\n§cUse §6Paper§c or a §6Paper fork§c for true stacking support.");
        }
    }

    private boolean isSpigotServer() {
        // Detect Paper by presence of Paper config class
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return false; // It's Paper
        } catch (ClassNotFoundException e) {
            return true; // Not Paper (likely Spigot or fork)
        }
    }

    private boolean isTotemStackingEnabled() {
        // Try to get the config value from the plugin's config
        try {
            return plugin.getConfig().getBoolean("totem-stacking.enabled", false);
        } catch (Exception e) {
            return false;
        }
    }

    private String fetchLatestSpigotVersion() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + SPIGOT_RESOURCE_ID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                return reader.readLine();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to fetch latest version from Spigot: " + e.getMessage());
            return null;
        }
    }

    private void sendUpdateMessage(CommandSender sender) {
        String prefix = "§e[TotemTweak] A new update is available! " +
                "§bCurrent: §f" + currentVersion + " §bLatest: §a" + latestVersion;
        String download = "§eDownload: ";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(prefix);

            TextComponent spigot = new TextComponent("§9[Spigot]");
            spigot.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, SPIGOT_URL));
            spigot.setUnderlined(true);

            TextComponent modrinth = new TextComponent("§a[Modrinth]");
            modrinth.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, MODRINTH_URL));
            modrinth.setUnderlined(true);

            TextComponent space = new TextComponent(" §7| ");
            TextComponent msg = new TextComponent(download);
            msg.addExtra(spigot);
            msg.addExtra(space);
            msg.addExtra(modrinth);

            player.spigot().sendMessage(msg);
        } else {
            // Console fallback
            sender.sendMessage(prefix);
            sender.sendMessage(download + "[Spigot] " + SPIGOT_URL + " | [Modrinth] " + MODRINTH_URL);
        }
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
} 