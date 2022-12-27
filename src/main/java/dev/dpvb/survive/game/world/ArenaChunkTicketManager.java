package dev.dpvb.survive.game.world;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.game.world.util.AsyncChunkLoadUtil;
import dev.dpvb.survive.game.world.util.ChunkCoordinate;
import dev.dpvb.survive.game.world.util.ConfigChunkCalculator;
import dev.dpvb.survive.util.computation.AsyncWait;

import java.util.LinkedList;

/**
 * Manages chunk tickets in the arena world.
 * <p>
 * Adding a chunk ticket makes the server keep a chunk loaded even in the
 * absence of players. This is useful for managing the arena world, as item
 * drops will not otherwise be detected if the chunk is not loaded.
 *
 * @author ms5984
 */
public class ArenaChunkTicketManager {
    private final LinkedList<ChunkCoordinate> arenaChunks = new LinkedList<>();
    private final ConfigChunkCalculator util;
    private boolean added;
    private boolean adding;

    /**
     * Creates a new chunk ticket manager.
     *
     * @param manager the game manager
     */
    public ArenaChunkTicketManager(GameManager manager) {
        this.util = new ConfigChunkCalculator(manager.getArenaWorld());
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
     * Calculates and reloads the list of chunks to be ticketed.
     *
     * @throws IllegalStateException if tickets have not been removed
     * @see #clearTickets()
     */
    public void calculate() throws IllegalStateException {
        if (adding) throw new IllegalStateException("Cannot calculate tickets while loading");
        if (added) throw new IllegalStateException("Tickets are currently added--run clearTickets() first.");
        synchronized (arenaChunks) {
            arenaChunks.clear();
            arenaChunks.addAll(util.loadCenter(Survive.Configuration.getArenaConfigSection()).getValidChunks());
        }
    }

    /**
     * Adds tickets to all chunks.
     *
     * @implNote No-op if tickets are being adding, have already been added,
     * or if the list of chunks to ticket is empty.
     * @see #isAdded()
     */
    public void addTickets() {
        if (added) return;
        if (adding) return;
        synchronized (arenaChunks) {
            if (arenaChunks.isEmpty()) return;
        }
        add();
    }

    /**
     * Adds tickets to all chunks, performing a task as soon as possible after.
     *
     * @param task a task to perform
     * @implNote If no tickets have been or will be added, <code>task</code>
     * will not run. If any tickets have already been added, <code>task</code>
     * will run immediately. If tickets are currently being added, this method
     * will schedule <code>task</code> to run after
     */
    public void addTicketsThen(Runnable task) {
        if (added) {
            task.run();
        } else {
            // no tickets have been added, check for adding
            if (!adding) {
                // no tickets are being added, add them
                synchronized (arenaChunks) {
                    if (arenaChunks.isEmpty()) return; // no-op
                    // add tickets
                    add();
                }
            }
            // Wait for adding to finish, then run the task
            new AsyncWait(() -> !adding, task).runTaskTimer(Survive.getInstance(), 0, 1);
        }
    }

    private void add() {
        final AsyncChunkLoadUtil load;
        synchronized (arenaChunks) {
            if (adding) throw new IllegalStateException();
            adding = true;
            load = new AsyncChunkLoadUtil(util.getWorld(), arenaChunks);
        }
        load.runTaskTimer(Survive.getInstance(), 0, 1);
        new AsyncWait(load::isComplete, this::unlock).runTaskTimer(Survive.getInstance(), 0, 1);
    }

    private void unlock() {
        synchronized (arenaChunks) {
            added = true;
            adding = false;
        }
    }

    /**
     * Clears all tickets from the arena world.
     *
     * @implNote No-op if no tickets are currently added or being added.
     * @see #isAdded()
     */
    public void clearTickets() {
        if (!added) return;
        if (adding) {
            // clear as soon as possible
            new AsyncWait(() -> !adding, this::clear).runTaskTimer(Survive.getInstance(), 0, 1);
            return;
        }
        clear();
    }

    private void clear() {
        util.getWorld().removePluginChunkTickets(Survive.getInstance());
        synchronized (arenaChunks) {
            added = false; // no matter the complexity of the previous state there are no tickets added now
        }
    }
}
