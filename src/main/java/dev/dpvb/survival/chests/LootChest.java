package dev.dpvb.survival.chests;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.mongo.models.ChestData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;

public class LootChest {

    private Block block;
    private ChestData chestData;
    private boolean exists;
    private InventoryWrapper inventory;

    public LootChest(Block block, ChestData chestData) {
        this.block = block;
        this.chestData = chestData;
        spawnChest();
    }

    public void spawnChest() {
        exists = true;
        inventory = new LootChestInventory(this).register();
        block.setType(chestData.getTier().getChestMaterial());
        BlockData data = block.getBlockData();
        if (data instanceof Directional dir) {
            dir.setFacing(chestData.getFace());
        }
        block.setBlockData(data);
    }

    public void open(Player player) {
        // Open the inventory for the player.
        player.openInventory(inventory.getInventory());

        // Visually open the block.
        BlockState state = block.getState();
        if (state instanceof Lidded lidded) {
            if (!lidded.isOpen()) {
                lidded.open();
            }
        }
    }

    public void destroy() {
        exists = false;
        inventory.unregister();
        block.setType(Material.AIR);
        block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_WOOD_BREAK, 1f, 1f);
    }

    public InventoryWrapper getInventory() {
        return inventory;
    }

    public Block getBlock() {
        return block;
    }

    public ChestTier getTier() {
        return chestData.getTier();
    }
}
