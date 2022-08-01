package dev.dpvb.survival.chests.airdrop;

import com.destroystokyo.paper.ParticleBuilder;
import dev.dpvb.survival.Survival;
import dev.dpvb.survival.chests.ChestTier;
import dev.dpvb.survival.chests.LootChest;
import dev.dpvb.survival.mongo.models.ChestData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class AirdropChest extends LootChest implements Listener {

    public AirdropChest(Block block) {
        super(block, new ChestData(
                block.getX(),
                block.getY(),
                block.getZ(),
                block.getWorld().getName(),
                BlockFace.NORTH,
                ChestTier.THREE)
        );
        spawnChest();
        Bukkit.getPluginManager().registerEvents(this, Survival.getInstance());
    }

    @Override
    public void destroy() {
        // Destroy the Chest
        inventory.unregister();
        HandlerList.unregisterAll(this);
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
    }

    @EventHandler
    public void onAirdropOpen(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!event.getClickedBlock().equals(block)) {
            return;
        }

        open(event.getPlayer());
    }
}
