package org.bischofftv.totemTweak.listeners;

import org.bischofftv.totemTweak.TotemTweak;
import org.bischofftv.totemTweak.utils.ConfigManager;
import org.bischofftv.totemTweak.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
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
        if (!configManager.isEntityDeathPrevented()) {
            return;
        }

        // Remove totem of undying from drops
        event.getDrops().removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);
        
        if (configManager.isDebug()) {
            String source = event.getEntityType().name();
            messageManager.sendConsoleMessage("totem-prevented", 
                new String[]{"source", "entity-death: " + source});
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDropItem(EntityDropItemEvent event) {
        if (!configManager.isEntityDropPrevented()) {
            return;
        }

        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem.getType() == Material.TOTEM_OF_UNDYING) {
            event.setCancelled(true);
            
            if (configManager.isDebug()) {
                String source = event.getEntityType().name();
                messageManager.sendConsoleMessage("totem-prevented", 
                    new String[]{"source", "entity-drop: " + source});
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!configManager.isPlayerDeathPrevented()) {
            return;
        }

        // Remove totem of undying from drops
        event.getDrops().removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);
        
        if (configManager.isDebug()) {
            String source = event.getEntity().getName();
            messageManager.sendConsoleMessage("totem-prevented", 
                new String[]{"source", "player-death: " + source});
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!configManager.isPlayerDropPrevented()) {
            return;
        }

        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem.getType() == Material.TOTEM_OF_UNDYING) {
            event.setCancelled(true);
            
            if (configManager.isDebug()) {
                String source = event.getPlayer().getName();
                messageManager.sendConsoleMessage("totem-prevented", 
                    new String[]{"source", "player-drop: " + source});
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!configManager.isMinecartDestructionPrevented()) {
            return;
        }

        if (event.getVehicle() instanceof StorageMinecart) {
            StorageMinecart minecart = (StorageMinecart) event.getVehicle();
            // Remove totems from storage minecart inventory
            minecart.getInventory().remove(Material.TOTEM_OF_UNDYING);
            
            if (configManager.isDebug()) {
                messageManager.sendConsoleMessage("totem-prevented", 
                    new String[]{"source", "minecart-destruction"});
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        
        // Check specific container types
        if (blockType == Material.CHEST && configManager.isChestDestructionPrevented()) {
            handleContainerBreak(event, "chest-destruction");
        } else if (blockType == Material.HOPPER && configManager.isHopperDestructionPrevented()) {
            handleContainerBreak(event, "hopper-destruction");
        } else if (blockType == Material.DISPENSER && configManager.isDispenserDestructionPrevented()) {
            handleContainerBreak(event, "dispenser-destruction");
        } else if (blockType == Material.DROPPER && configManager.isDropperDestructionPrevented()) {
            handleContainerBreak(event, "dropper-destruction");
        } else if (blockType == Material.BARREL && configManager.isBarrelDestructionPrevented()) {
            handleContainerBreak(event, "barrel-destruction");
        } else if (configManager.isBlockBreakPrevented()) {
            // General block break prevention
            handleContainerBreak(event, "block-break");
        }
    }

    private void handleContainerBreak(BlockBreakEvent event, String sourceType) {
        BlockState blockState = event.getBlock().getState();
        if (blockState instanceof Container) {
            Container container = (Container) blockState;
            // Remove totems from container inventory
            container.getInventory().remove(Material.TOTEM_OF_UNDYING);
            
            if (configManager.isDebug()) {
                messageManager.sendConsoleMessage("totem-prevented", 
                    new String[]{"source", sourceType});
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!configManager.isExplosionPrevented()) {
            return;
        }

        // Handle explosion events - this is more complex and might need additional handling
        // For now, we'll log it in debug mode
        if (configManager.isDebug()) {
            messageManager.sendConsoleMessage("totem-prevented", 
                new String[]{"source", "explosion"});
        }
    }
} 