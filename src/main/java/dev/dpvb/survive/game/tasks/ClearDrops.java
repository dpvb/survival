package dev.dpvb.survive.game.tasks;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.util.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearDrops extends BukkitRunnable {

    private final GameManager manager;
    private boolean playerSinceLastClear;

    public ClearDrops(GameManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        if (playerSinceLastClear) {
            manager.sendMessage(Messages.CLEARING_DROPS_WARNING);
            // have the server async load chunks ahead of our trying to clear them
            manager.getArenaChunkTicketManager().addTickets();
        }
        Bukkit.getScheduler().runTaskLater(Survive.getInstance(), this::despawnItems, 20L * 30);
    }

    public void playerSinceLastClear() {
        playerSinceLastClear = true;
    }

    private void despawnItems() {
        if (playerSinceLastClear) {
            // send message once clear is complete (in case it doesn't happen immediately)
            manager.clearDropsOnGround().thenRunAsync(() -> manager.sendMessage(Messages.DESPAWNED_DROPS));
        } else {
            Messages.CLEARING_DROPS_SKIP.sendConsole();
        }
        final var noPlayers = manager.getPlayers().isEmpty();
        // if we had players, but now we don't, tell console we're skipping future clears
        if (playerSinceLastClear && noPlayers) {
            Messages.CLEARING_DROPS_SKIP_NEXT.sendConsole();
        }
        // refresh state
        playerSinceLastClear = !noPlayers;
    }
}
