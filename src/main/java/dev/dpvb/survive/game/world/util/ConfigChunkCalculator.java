package dev.dpvb.survive.game.world.util;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Calculates chunks which should be loaded according to configuration.
 *
 * @author ms5984
 */
public class ConfigChunkCalculator {
    private final World world;
    private Vector center;
    private double size;

    /**
     * Creates a new config chunk calculator for the given world.
     *
     * @param world the world to calculate chunks for
     */
    public ConfigChunkCalculator(@NotNull World world) {
        this.world = world;
    }

    /**
     * Gets the world in which chunks will be calculated.
     *
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Loads the center and size from the given configuration section.
     *
     * @param section a configuration section
     * @return this calculator
     */
    public ConfigChunkCalculator loadCenter(@NotNull ConfigurationSection section) {
        center = new Vector(section.getDouble("center.x"), 0d, section.getDouble("center.z"));
        size = section.getDouble("size");
        return this;
    }

    ChunkCoordinate get_XZ_min() {
        return new ChunkCoordinate(
                world.getBlockAt(
                    min(center.getX(), size),
                    center.getBlockY(),
                    min(center.getZ(), size)
                ).getChunk()
        );
    }

    ChunkCoordinate get_XZ_max() {
        return new ChunkCoordinate(
                world.getBlockAt(
                    max(center.getX(), size),
                    center.getBlockY(),
                    max(center.getZ(), size)
                ).getChunk()
        );
    }

    /**
     * Gets the list of all chunks within the world border represented as
     * chunk coordinates.
     *
     * @return a newly-calculated list of chunk coordinates
     * @implNote The returned list might not support edits.
     */
    public @NotNull List<ChunkCoordinate> getValidChunks() {
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
     * Gets the minimum block for the provided axis center and size.
     *
     * @param c axis center
     * @param s side length
     * @return minimum block
     */
    static int min(double c, double s) {
        return NumberConversions.floor(c - Math.ceil(s / 2d));
    }

    /**
     * Gets the maximum block for the provided axis center and size.
     *
     * @param c axis center
     * @param s side length
     * @return maximum block
     */
    static int max(double c, double s) {
        return NumberConversions.floor(c + Math.floor(s / 2d));
    }

}
