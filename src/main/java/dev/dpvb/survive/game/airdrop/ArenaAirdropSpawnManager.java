package dev.dpvb.survive.game.airdrop;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Manages natural airdrop spawning in the arena world.
 *
 * @author ms5984
 */
public class ArenaAirdropSpawnManager {
    /**
     * Tests spawn location fitness--whether it is far enough from a list of players.
     *
     * @param players a list of vectors (player locations)
     * @param location the location to test
     * @param minDistance the minimum distance required
     * @return true if the location is far enough from all players
     */
    public static boolean testLocationFitness(@NotNull List<Vector> players, @NotNull Location location, double minDistance) {
        final var loc = location.toVector();
        for (Vector player : players) {
            if (player.distance(loc) < minDistance) {
                return false;
            }
        }
        return true;
    }
}
