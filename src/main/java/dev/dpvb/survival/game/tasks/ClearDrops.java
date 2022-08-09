package dev.dpvb.survival.game.tasks;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.game.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearDrops extends BukkitRunnable {

    private final GameManager manager;

    public ClearDrops(GameManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        manager.broadcast(Component.text("Item drops will be despawned in 30 seconds.").color(NamedTextColor.RED));
        Bukkit.getScheduler().runTaskLater(Survival.getInstance(), this::despawnItems, 20L * 30);
    }

    private void despawnItems() {
        manager.clearDropsOnGround();
        manager.broadcast(Component.text("Item drops despawned.").color(NamedTextColor.RED));
    }
}
