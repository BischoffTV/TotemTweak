package org.bischofftv.totemTweak.listeners;

import org.bischofftv.totemTweak.TotemTweak;
import org.bischofftv.totemTweak.utils.ConfigManager;
import org.bischofftv.totemTweak.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class StrongholdChestListener implements Listener {
    private final TotemTweak plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final Random random;

    public StrongholdChestListener(TotemTweak plugin, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.random = new Random();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        // Check if it's a chest inventory
        if (!(event.getInventory().getHolder() instanceof Chest)) {
            return;
        }

        Chest chest = (Chest) event.getInventory().getHolder();
        Block block = chest.getBlock();
        
        // Check if this chest is in a stronghold (by checking surrounding blocks for stronghold materials)
        if (isStrongholdChest(block)) {
            // Check if totem should be added based on chance
            if (random.nextDouble() <= configManager.getTotemDropChance()) {
                // Check if chest already has a totem
                if (!hasTotem(event.getInventory())) {
                    ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
                    event.getInventory().addItem(totem);
                    
                    if (configManager.isDebug()) {
                        String location = block.getWorld().getName() + ", " + 
                                        block.getX() + ", " + block.getY() + ", " + block.getZ();
                        messageManager.sendConsoleMessage("totem-added", 
                            new String[]{"location", location});
                    }
                }
            }
        }
    }

    private boolean isStrongholdChest(Block block) {
        // Check for stronghold indicators in a radius around the chest
        for (int x = -10; x <= 10; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -10; z <= 10; z++) {
                    Block relative = block.getRelative(x, y, z);
                    Material type = relative.getType();
                    
                    // Stronghold indicators: stone bricks, mossy stone bricks, cracked stone bricks
                    if (type == Material.STONE_BRICKS || 
                        type == Material.MOSSY_STONE_BRICKS || 
                        type == Material.CRACKED_STONE_BRICKS ||
                        type == Material.INFESTED_STONE_BRICKS) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasTotem(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
                return true;
            }
        }
        return false;
    }
} 