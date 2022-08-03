package dev.dpvb.survival.chests;

import com.destroystokyo.paper.ParticleBuilder;
import dev.dpvb.survival.gui.InventoryWrapper;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Lidded;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class LootableChest {
    protected transient final Block block;
    protected final Material material;
    protected final BlockFace facing;
    protected InventoryWrapper wrapper;

    protected LootableChest(@NotNull Block block, Material material, BlockFace facing) {
        this.block = block;
        this.material = material;
        this.facing = facing;
    }

    public @NotNull abstract LootSource getLootSource();

    public Sound getBreakSound() {
        // Default to wood break sound
        return Sound.BLOCK_WOOD_BREAK;
    }

    public void spawnChest() {
        wrapper = new LootChestInventory(this).register();
        block.setType(material);
        final var data = block.getBlockData();
        if (data instanceof Directional dir) {
            dir.setFacing(facing);
        }
        block.setBlockData(data);
    }

    public void open(Player player) {
        // Open the inventory for the player.
        player.openInventory(wrapper.getInventory());

        // Visually open the block.
        final var state = block.getState();
        if (state instanceof Lidded lidded) {
            if (!lidded.isOpen()) {
                lidded.open();
            }
        }
    }

    public void close() {
        // Visually close the block.
        final var state = block.getState();
        if (state instanceof Lidded lidded) {
            if (lidded.isOpen()) {
                lidded.close();
            }
        }
    }

    public void destroy() {
        // Unregister wrapper
        wrapper.unregister();

        // capture block data for particle effect
        final var data = block.getBlockData();

        block.setType(Material.AIR);

        // Play Sound
        block.getLocation().getWorld().playSound(block.getLocation(), getBreakSound(), 1f, 1f);

        // Create Particles
        new ParticleBuilder(Particle.BLOCK_DUST)
                .data(data)
                .location(block.getLocation())
                .offset(0.5, 0.5, 0.5)
                .receivers(10)
                .count(20)
                .spawn();
    }

    public final @NotNull Block getBlock() {
        return block;
    }
}
