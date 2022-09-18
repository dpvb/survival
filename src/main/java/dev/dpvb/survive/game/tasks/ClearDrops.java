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
        // have the server async load chunks ahead of our trying to clear them
        manager.getArenaChunkTicketManager().addTickets();
        Bukkit.getScheduler().runTaskLater(Survive.getInstance(), this::despawnItems, 20L * 30);
    }

    private void despawnItems() {
        // send message once clear is complete (in case it doesn't happen immediately)
        manager.clearDropsOnGround().thenRunAsync(() -> manager.sendMessage(Messages.DESPAWNED_DROPS));
    }
}
