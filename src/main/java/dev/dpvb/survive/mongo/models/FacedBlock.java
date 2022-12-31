package dev.dpvb.survive.mongo.models;

import org.bukkit.block.BlockFace;

@SuppressWarnings("unused") // Mongo model
public class FacedBlock {

    private int x;
    private int y;
    private int z;
    private BlockFace face;

    public FacedBlock() {}

    public FacedBlock(int x, int y, int z, BlockFace face) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public BlockFace getFace() {
        return face;
    }

    public void setFace(BlockFace face) {
        this.face = face;
    }
}
