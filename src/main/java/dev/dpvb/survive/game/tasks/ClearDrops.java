package dev.dpvb.survive.game.tasks;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.util.messages.Messages;
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
        // we need to make sure we load the chunks before we try to clear them
        manager.getArenaChunkTicketManager().addTickets();
        Bukkit.getScheduler().runTaskLater(Survive.getInstance(), this::despawnItems, 20L * 30);
    }

    private void despawnItems() {
        manager.clearDropsOnGround();
        manager.sendMessage(Messages.DESPAWNED_DROPS);
    }
}
