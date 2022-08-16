package dev.dpvb.survive.mongo.models;

import dev.dpvb.survive.chests.tiered.ChestTier;
import org.bukkit.block.BlockFace;

@SuppressWarnings("unused") // Mongo model
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setFace(BlockFace face) {
        this.face = face;
    }

    public void setTier(ChestTier tier) {
        this.tier = tier;
    }
}
