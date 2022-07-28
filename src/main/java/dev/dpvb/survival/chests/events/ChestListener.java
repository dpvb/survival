package dev.dpvb.survival.chests.events;

import dev.dpvb.survival.chests.ChestManager;
import dev.dpvb.survival.chests.LootChest;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class ChestListener implements Listener {

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        InventoryType type = event.getInventory().getType();
        if (type == InventoryType.ENDER_CHEST || type == InventoryType.CHEST) {
            // Don't open the normal inventory
            event.setCancelled(true);

            // Open the lootchests inventory
            Location location = event.getInventory().getLocation();
            LootChest lootChest = ChestManager.getInstance().getLootChestMap().get(location);

            event.getPlayer().openInventory(lootChest.getInventory().getInventory());
        }
    }

}
