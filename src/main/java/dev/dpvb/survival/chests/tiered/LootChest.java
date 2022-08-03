package dev.dpvb.survival.chests.tiered;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.chests.LootSource;
import dev.dpvb.survival.chests.LootableChest;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class LootChest extends LootableChest {
    private static final int RESPAWN_TIME_MIN = 15; // in seconds
    private static final int RESPAWN_TIME_MAX = 45; // in seconds

    final ChestTier tier;

    public LootChest(@NotNull Block block, ChestTier tier, BlockFace facing) {
        super(block, tier.getChestMaterial(), facing);
        this.tier = tier;
        spawnChest();
    }

    @Override
    public @NotNull LootSource getLootSource() {
        return tier;
    }

    @Override
    public Sound getBreakSound() {
        return tier.getBreakSound();
    }

    @Override
    public void destroy() {
        super.destroy();
        // Queue respawn
        final int respawnTime = ThreadLocalRandom.current().nextInt(RESPAWN_TIME_MIN, RESPAWN_TIME_MAX);
        // Nest sync in async to increase timing accuracy
        Bukkit.getScheduler().runTaskLaterAsynchronously(
                Survival.getInstance(),
                () -> Bukkit.getScheduler().runTask(Survival.getInstance(), LootChest.this::spawnChest),
                respawnTime * 20L
        );
    }
}
