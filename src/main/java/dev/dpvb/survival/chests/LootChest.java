package dev.dpvb.survival.chests;

import dev.dpvb.survival.Survival;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class LootChest extends LootableChest {
    private static final int RESPAWN_TIME_MIN = 15; // in seconds
    private static final int RESPAWN_TIME_MAX = 45; // in seconds

    public LootChest(@NotNull Block block, Material material, BlockFace facing) {
        super(block, material, facing);
        spawnChest();
    }

    @Override
    public @NotNull LootSource getLootSource() {
        return ChestTier.getTier(material);
    }

    @Override
    public Sound getBreakSound() {
        return switch (material) {
            case CHEST -> Sound.BLOCK_WOOD_BREAK;
            case ENDER_CHEST -> Sound.BLOCK_STONE_BREAK;
            default -> super.getBreakSound();
        };
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
