package dev.dpvb.survive.chests;

import dev.dpvb.survive.gui.InventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class LootableChestInventory extends InventoryWrapper {

    private final LootableChest chest;
    private final int size;

    public LootableChestInventory(LootableChest chest) {
        this.chest = chest;
        this.size = 27;
    }

    @Override
    protected Inventory generateInventory() {
        // Create the loot that should be spawned for this item.
        final var items = chest.getLootSource().generateLoot();

        // Create the Inventory
        Inventory inventory = Bukkit.createInventory(null, size);
        for (ItemStack item : items) {
            int slot;
            do {
                slot = ThreadLocalRandom.current().nextInt(0, size);
            } while (inventory.getItem(slot) != null);
            inventory.setItem(slot, item);
        }
        return inventory;
    }

    @Override
    public void handle(InventoryCloseEvent event) {
        if (event.getInventory().getViewers().size() == 1) {
            if (event.getInventory().isEmpty()) {
                chest.destroy();
            } else {
                chest.close();
            }
        }
    }
}
