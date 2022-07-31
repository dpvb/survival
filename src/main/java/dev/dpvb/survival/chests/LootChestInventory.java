package dev.dpvb.survival.chests;


import dev.dpvb.survival.gui.InventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class LootChestInventory extends InventoryWrapper {

    private LootChest chest;
    private int size;

    public LootChestInventory(LootChest chest) {
        this.chest = chest;
        this.size = 9;
    }

    @Override
    protected Inventory generateInventory() {
        // Create the loot that should be spawned for this item.
        Set<ItemStack> items = chest.getTier().generateLoot();

        // Create the Inventory
        Inventory inventory = Bukkit.createInventory(null, size);
        for (ItemStack item : items) {
            inventory.addItem(item);
        }
        return inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {

    }

    @Override
    public void handle(InventoryCloseEvent event) {
        if (event.getInventory().getViewers().size() == 1) {
            if (event.getInventory().isEmpty()) {
                chest.destroy();
            } else {
                BlockState state = chest.getBlock().getState();
                if (state instanceof Lidded lidded) {
                    lidded.close();
                }
            }
        }
    }
}
