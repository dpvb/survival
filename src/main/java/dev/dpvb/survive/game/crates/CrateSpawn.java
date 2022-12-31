package dev.dpvb.survive.game.crates;

import dev.dpvb.survive.game.GameManager;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents the spawn location of a crate.
 */
public record CrateSpawn(int x, int y, int z) {
    /**
     * Gets this spawn as a {@link Location} in the arena world.
     * <p>
     *
     * @return a corresponding location
     */
    public Location asLocation() {
        final World arenaWorld = GameManager.getInstance().getArenaWorld();
        return new Location(arenaWorld, x, y, z);
    }
}
