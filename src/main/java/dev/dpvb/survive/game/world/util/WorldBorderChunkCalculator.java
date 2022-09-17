package dev.dpvb.survive.game.world.util;

import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class WorldBorderChunkCalculator {
    private final World world;
    private Vector center;
    private double length;

    public WorldBorderChunkCalculator(@NotNull World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public WorldBorderChunkCalculator loadCenter() {
        // world border is x = 0.5, z = 80.5; radius = 900
        center = world.getWorldBorder().getCenter().toVector();
        length = world.getWorldBorder().getSize();
        return this;
    }

    ChunkCoordinate get_XZ_min() {
        // should be [-29, -24]
        return new ChunkCoordinate(
                world.getBlockAt(
                    min(center.getX(), length),
                    center.getBlockY(),
                    min(center.getZ(), length)
                ).getChunk()
        );
    }

    ChunkCoordinate get_XZ_max() {
        // should be [28, 33]
        return new ChunkCoordinate(
                world.getBlockAt(
                    max(center.getX(), length),
                    center.getBlockY(),
                    max(center.getZ(), length)
                ).getChunk()
        );
    }

    /**
     * Get the list of all chunks within the world border represented as
     * chunk coordinates.
     *
     * @return newly-calculated list of chunk coordinates
     * @implNote The returned list may not support edits.
     */
    public List<ChunkCoordinate> getValidChunks() {
        // if we haven't loaded the center, we can't do anything
        if (center == null) return List.of();
        final List<ChunkCoordinate> list = new LinkedList<>();
        final var min = get_XZ_min();
        final var max = get_XZ_max();
        // add all chunks in the range
        for (int x = min.x(); x <= max.x(); ++x) {
            for (int z = min.z(); z <= max.z(); ++z) {
                list.add(new ChunkCoordinate(x, z));
            }
        }
        return list;
    }

    /**
     * Get the minimum block for the provided axis center
     * based on the total border length.
     *
     * @param c axis center
     * @param d length of the world border
     * @return minimum block
     */
    static int min(double c, double d) {
        return NumberConversions.floor(c - Math.ceil(d / 2d));
    }

    /**
     * Get the maximum block for the provided axis center
     * based on the total border length.
     *
     * @param c axis center
     * @param d length of the world border
     * @return maximum block
     */
    static int max(double c, double d) {
        return NumberConversions.floor(c + Math.floor(d / 2d));
    }

}
