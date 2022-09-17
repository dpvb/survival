package dev.dpvb.survive.game.world;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.game.world.util.WorldBorderChunkCalculator;
import dev.dpvb.survive.game.world.util.ChunkCoordinate;

import java.util.LinkedList;

public class ArenaChunkTicketManager {
    private final LinkedList<ChunkCoordinate> arenaChunks = new LinkedList<>();
    private final WorldBorderChunkCalculator util;
    private boolean added;

    public ArenaChunkTicketManager(GameManager manager) {
        this.util = new WorldBorderChunkCalculator(manager.getArenaWorld());
    }

    /**
     * Whether plugin chunk tickets have been added.
     *
     * @return true if tickets have been added, false otherwise
     */
    public boolean isAdded() {
        return added;
    }

    /**
     * Calculate and reload the list of chunks to be ticketed.
     *
     * @throws IllegalStateException if tickets have not been removed
     * @see #clearTickets()
     */
    public void calculate() throws IllegalStateException {
        if (added) throw new IllegalStateException("Tickets are currently added--run clearTickets() first.");
        synchronized (arenaChunks) {
            arenaChunks.clear();
            arenaChunks.addAll(util.loadCenter().getValidChunks());
        }
    }

    /**
     * Add tickets to all chunks within the arena's world border.
     *
     * @implNote No-op if tickets have already been added.
     * @see #isAdded()
     */
    public void addTickets() {
        if (added) return;
        synchronized (arenaChunks) {
            arenaChunks.forEach(c -> util.getWorld().getChunkAtAsync(c.x(), c.z())
                    .thenAccept(loaded -> loaded.addPluginChunkTicket(Survive.getInstance())));
            added = true;
        }
    }

    /**
     * Clear all Survive tickets from the arena world.
     *
     * @implNote No-op if no tickets are currently added.
     * @see #isAdded()
     */
    public void clearTickets() {
        if (!added) return;
        util.getWorld().removePluginChunkTickets(Survive.getInstance());
        synchronized (arenaChunks) {
            added = false;
        }
    }
}
