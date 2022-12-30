package dev.dpvb.survive.chests;

import com.destroystokyo.paper.ParticleBuilder;
import dev.dpvb.survive.Survive;
import dev.dpvb.survive.gui.InventoryWrapper;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Lidded;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class LootableChest {
    protected transient final Block block;
    protected final Material material;
    protected final BlockFace facing;
    protected InventoryWrapper wrapper;

    protected final static HolographicDisplaysAPI holoAPI = Survive.getHoloAPI();
    protected Hologram hologram;

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
        wrapper = new LootableChestInventory(this).register();
        block.setType(material);
        final var data = block.getBlockData();
        if (data instanceof Directional dir) {
            dir.setFacing(facing);
        }
        block.setBlockData(data);

        generateHologram();
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

        // Remove hologram
        deleteHologram();

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

    protected void generateHologram() {
        final Vector hologramOffset = new Vector(0.5, 1.5, 0.5);
        hologram = holoAPI.createHologram(block.getLocation().add(hologramOffset));
        hologram.getLines().appendText(hologramText());
    }

    public void deleteHologram() {
        if (hologram != null) {
            hologram.delete();
        }
    }

    protected abstract String hologramText();
}
