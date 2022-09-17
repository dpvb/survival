package dev.dpvb.survive.game.world.util;

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public record ChunkCoordinate(int x, int z) implements Comparable<ChunkCoordinate> {
    ChunkCoordinate(Chunk chunk) {
        this(chunk.getX(), chunk.getZ());
    }

    @Override
    public int compareTo(@NotNull ChunkCoordinate o) {
        if (this.equals(o)) return 0;
        return (x < o.x && z < o.z) ? -1 : 1;
    }
}
