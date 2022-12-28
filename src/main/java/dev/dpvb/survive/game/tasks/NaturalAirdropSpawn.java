package dev.dpvb.survive.game.tasks;

import dev.dpvb.survive.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Spawns an airdrop at a random location in the arena.
 *
 * @author ms5984
 */
public class NaturalAirdropSpawn extends BukkitRunnable {
    private final GameManager manager;
    private boolean spawned;

    public NaturalAirdropSpawn(GameManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        if (!spawned && !isCancelled()) {
            // TODO
        }
    }

    public Location mutateToHighestYAtXZ(@NotNull Location location) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This method must be called from the main thread");
        }
        // mutate location
        location.setY(location.getWorld().getHighestBlockYAt(location));
        return location;
    }
}
