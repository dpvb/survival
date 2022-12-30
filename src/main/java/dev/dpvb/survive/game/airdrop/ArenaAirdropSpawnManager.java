package dev.dpvb.survive.game.airdrop;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.game.tasks.NaturalAirdropSpawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Manages natural airdrop spawning in the arena world.
 *
 * @author ms5984
 */
public class ArenaAirdropSpawnManager {
    final GameManager manager;
    private final NextAirdropCalculator calculator;
    private final RandomLocationGenerator locationGenerator;
    private List<Location> cachedProtoSpawns;
    private BukkitTask task;

    public ArenaAirdropSpawnManager(GameManager manager) {
        this.manager = manager;
        final var frequencySection = Survive.Configuration.getAirdropSpawningSection().getConfigurationSection("frequency");
        if (frequencySection == null) {
            throw new IllegalStateException("Missing frequency subsection");
        }
        this.calculator = new NextAirdropCalculator(frequencySection);
        this.locationGenerator = new RandomLocationGenerator(this::getCachedProtoSpawns);
    }

    /**
     * Schedules the next natural airdrop spawn.
     *
     * @return the time in minutes until the next natural airdrop spawn
     */
    public int scheduleNextDrop() {
        final int nextAirdrop;
        synchronized (this) {
            nextAirdrop = calculator.calculate(manager.getPlayerCount());
            task = Bukkit.getScheduler().runTaskLater(Survive.getInstance(), this::scheduleDrop, 20L * nextAirdrop * 60L);
        }
        return nextAirdrop;
    }

    /**
     * Stops the current schedule of natural airdrop spawning.
     */
    public void stopScheduling() {
        synchronized (this) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
            task = null;
        }
    }

    private void scheduleDrop() {
        // Starts a sync task to spawn the airdrop
        new NaturalAirdropSpawn(this).runTaskTimer(Survive.getInstance(), 0L, 1L);
    }

    private List<Location> getCachedProtoSpawns() {
        if (cachedProtoSpawns == null) {
            throw new IllegalStateException("Spawn locations not yet cached");
        }
        return cachedProtoSpawns;
    }

    /**
     * Sets the cached list of proto-spawn locations.
     *
     * @param cachedSpawnLocations the new list of proto-spawn locations
     */
    public void setCachedProtoSpawns(List<Location> cachedSpawnLocations) {
        this.cachedProtoSpawns = cachedSpawnLocations;
    }

    /**
     * Gets the airdrop proto location generator util.
     *
     * @return the proto location generator
     */
    public RandomLocationGenerator getLocationGenerator() {
        return locationGenerator;
    }

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
