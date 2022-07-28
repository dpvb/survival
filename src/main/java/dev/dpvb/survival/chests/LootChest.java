package dev.dpvb.survival.chests;

import dev.dpvb.survival.gui.InventoryWrapper;
import org.bukkit.block.Block;

public class LootChest {

    private Block block;
    private ChestTier tier;
    private boolean exists;
    private InventoryWrapper inventory;

    public LootChest(Block block, ChestTier tier) {
        this.block = block;
        this.tier = tier;
        this.inventory = new LootChestInventory().register();
        this.exists = true;
    }

    public InventoryWrapper getInventory() {
        return inventory;
    }
}
