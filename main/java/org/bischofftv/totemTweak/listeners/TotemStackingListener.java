package org.bischofftv.totemTweak.listeners;

import org.bischofftv.totemTweak.TotemTweak;
import org.bischofftv.totemTweak.utils.ConfigManager;
import org.bischofftv.totemTweak.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class TotemStackingListener implements Listener {
    private final TotemTweak plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public TotemStackingListener(TotemTweak plugin, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!configManager.isTotemStackingEnabled()) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // Handle totem stacking when clicking with totems
        if (clickedItem != null && clickedItem.getType() == Material.TOTEM_OF_UNDYING) {
            if (cursorItem != null && cursorItem.getType() == Material.TOTEM_OF_UNDYING) {
                handleTotemStacking(event, clickedItem, cursorItem);
            }
        } else if (cursorItem != null && cursorItem.getType() == Material.TOTEM_OF_UNDYING) {
            if (clickedItem != null && clickedItem.getType() == Material.TOTEM_OF_UNDYING) {
                handleTotemStacking(event, clickedItem, cursorItem);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!configManager.isTotemStackingEnabled()) {
            return;
        }

        ItemStack draggedItem = event.getOldCursor();
        if (draggedItem != null && draggedItem.getType() == Material.TOTEM_OF_UNDYING) {
            // Ensure dragged totems don't exceed max stack size
            int maxStackSize = configManager.getMaxTotemStackSize();
            if (draggedItem.getAmount() > maxStackSize) {
                draggedItem.setAmount(maxStackSize);
                event.setCursor(draggedItem);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!configManager.isTotemStackingEnabled()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        ItemStack pickedUpItem = event.getItem().getItemStack();
        if (pickedUpItem.getType() == Material.TOTEM_OF_UNDYING) {
            int maxStackSize = configManager.getMaxTotemStackSize();
            if (pickedUpItem.getAmount() > maxStackSize) {
                pickedUpItem.setAmount(maxStackSize);
                event.getItem().setItemStack(pickedUpItem);
            }
        }
    }

    private void handleTotemStacking(InventoryClickEvent event, ItemStack clickedItem, ItemStack cursorItem) {
        int maxStackSize = configManager.getMaxTotemStackSize();
        int clickedAmount = clickedItem.getAmount();
        int cursorAmount = cursorItem.getAmount();
        int totalAmount = clickedAmount + cursorAmount;

        if (totalAmount <= maxStackSize) {
            // Can stack completely
            clickedItem.setAmount(totalAmount);
            event.setCursor(null);
            event.setCurrentItem(clickedItem);
        } else {
            // Partial stacking
            int spaceInClicked = maxStackSize - clickedAmount;
            if (spaceInClicked > 0) {
                clickedItem.setAmount(maxStackSize);
                cursorItem.setAmount(cursorAmount - spaceInClicked);
                event.setCurrentItem(clickedItem);
                event.setCursor(cursorItem);
            }
        }

        if (configManager.isDebug()) {
            messageManager.sendConsoleMessage("totem-stacked", 
                new String[]{"amount", String.valueOf(totalAmount)});
        }
    }
} 