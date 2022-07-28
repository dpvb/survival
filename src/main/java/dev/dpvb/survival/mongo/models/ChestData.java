package dev.dpvb.survival.mongo.models;

import dev.dpvb.survival.chests.ChestTier;
import org.bukkit.block.BlockFace;

public class ChestData {

    private int x;
    private int y;
    private int z;
    private String world;
    private BlockFace face;
    private ChestTier tier;

    public ChestData() {

    }

    public ChestData(int x, int y, int z, String world, BlockFace face, ChestTier tier) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.face = face;
        this.tier = tier;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public BlockFace getFace() {
        return face;
    }

    public ChestTier getTier() {
        return tier;
    }
}
