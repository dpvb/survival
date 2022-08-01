package dev.dpvb.survival.chests;

import com.destroystokyo.paper.ParticleBuilder;
import dev.dpvb.survival.Survival;
import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.mongo.models.ChestData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class LootChest {

    protected Block block;
    private ChestData chestData;
    protected InventoryWrapper inventory;
    private final int respawnTimeMin = 15; // in seconds
    private final int respawnTimeMax = 45; // in seconds

    public LootChest(Block block, ChestData chestData) {
        this.block = block;
        this.chestData = chestData;
        spawnChest();
    }

    public void spawnChest() {
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
        // Destroy the Chest
        inventory.unregister();
        BlockData data = block.getBlockData();
        block.setType(Material.AIR);

        // Play Sound
        block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_WOOD_BREAK, 1f, 1f);

        // Create Particles
        new ParticleBuilder(Particle.BLOCK_DUST)
                .data(data)
                .location(block.getLocation())
                .offset(0.5, 0.5, 0.5)
                .receivers(10)
                .count(20)
                .spawn();


        // Prep to spawn new Chest
        final int respawnTime = ThreadLocalRandom.current().nextInt(respawnTimeMin, respawnTimeMax);
        Bukkit.getScheduler().runTaskLater(Survival.getInstance(), this::spawnChest, respawnTime * 20L);

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
