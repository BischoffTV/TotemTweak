package org.bischofftv.totemTweak.listeners;

import org.bischofftv.totemTweak.TotemTweak;
import org.bischofftv.totemTweak.utils.ConfigManager;
import org.bischofftv.totemTweak.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class TotemDropListener implements Listener {
    private final TotemTweak plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public TotemDropListener(TotemTweak plugin, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!configManager.isPreventOtherDrops()) {
            return;
        }

        // Remove totem of undying from drops
        event.getDrops().removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);
        
        if (configManager.isDebug()) {
            String source = event.getEntityType().name();
            messageManager.sendConsoleMessage("totem-prevented", 
                new String[]{"source", source});
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDropItem(EntityDropItemEvent event) {
        if (!configManager.isPreventOtherDrops()) {
            return;
        }

        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem.getType() == Material.TOTEM_OF_UNDYING) {
            event.setCancelled(true);
            
            if (configManager.isDebug()) {
                String source = event.getEntityType().name();
                messageManager.sendConsoleMessage("totem-prevented", 
                    new String[]{"source", source});
            }
        }
    }
} 