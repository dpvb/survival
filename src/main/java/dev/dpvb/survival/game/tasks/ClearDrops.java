package dev.dpvb.survival.game.tasks;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.game.GameManager;
import dev.dpvb.survival.util.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearDrops extends BukkitRunnable {

    private final GameManager manager;

    public ClearDrops(GameManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        manager.sendMessage(Messages.CLEARING_DROPS_WARNING);
        Bukkit.getScheduler().runTaskLater(Survival.getInstance(), this::despawnItems, 20L * 30);
    }

    private void despawnItems() {
        manager.clearDropsOnGround();
        manager.sendMessage(Messages.DESPAWNED_DROPS);
    }
}
