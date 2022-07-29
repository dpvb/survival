package dev.dpvb.survival.chests;

import dev.dpvb.survival.gui.InventoryWrapper;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.entity.Player;

public class LootChest {

    private Block block;
    private ChestTier tier;
    private boolean exists;
    private InventoryWrapper inventory;

    public LootChest(Block block, ChestTier tier) {
        this.block = block;
        this.tier = tier;
        this.inventory = new LootChestInventory(this).register();
        this.exists = true;
    }

    public InventoryWrapper getInventory() {
        return inventory;
    }

    public void open(Player player) {
        // Open the inventory for the player.
        player.openInventory(inventory.getInventory());

        BlockState state = block.getState();
        if (state instanceof Lidded lidded) {
            if (!lidded.isOpen()) {
                lidded.open();
            }
        }
    }

    public Block getBlock() {
        return block;
    }
}
