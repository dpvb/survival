package dev.dpvb.survive.game.tasks;

import dev.dpvb.survive.game.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

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
}
