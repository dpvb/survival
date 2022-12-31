package dev.dpvb.survive.game.crates;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.chests.hackable.HackableChestManager;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.util.computation.BoundedRngCalculator;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Manages natural hackable crate spawning in the arena world.
 *
 * @author ms5984
 */
public class HackableCrateSpawnManager {
    private final GameManager manager;
    private final BoundedRngCalculator calculator;
    private List<CrateSpawn> spawns;
    private BukkitTask task;

    public HackableCrateSpawnManager(GameManager manager) {
        this.manager = manager;
        final var frequencySection = Survive.Configuration.getHackableCrateSpawningSection().getConfigurationSection("frequency");
        if (frequencySection == null) {
            throw new IllegalStateException("Missing frequency subsection");
        }
        this.calculator = new BoundedRngCalculator(frequencySection);
    }

    /**
     * Sets the list of spawn locations.
     *
     * @param spawns the new list of spawn locations
     */
    public void setSpawns(List<CrateSpawn> spawns) {
        this.spawns = spawns;
    }

    /**
     * Schedules regular spawning.
     * <p>
     * Spawning will continue until {@link #stopScheduling()} is called.
     *
     * @return the time in minutes until the next spawn
     */
    public int scheduleSpawns() {
        final int next;
        synchronized (this) {
            next = calculator.calculate(manager.getPlayerCount());
            task = Bukkit.getScheduler().runTaskLater(Survive.getInstance(), this::spawn, 20L * next * 60L);
        }
        return next;
    }

    /**
     * Stops the current scheduled spawning, if any.
     */
    public void stopScheduling() {
        synchronized (this) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
            task = null;
        }
    }

    private void spawn() {
        HackableChestManager.getInstance().spawnHackableChest(getRandomSpawn().asLocation());
    }

    private CrateSpawn getRandomSpawn() {
        if (spawns == null) {
            throw new IllegalStateException("Hackable crate spawns not initialized");
        }
        return spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));
    }
}
