package dev.dpvb.survive.game.world.util;

import dev.dpvb.survive.Survive;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Loads chunks asynchronously from a list of coordinates.
 *
 * @author ms5984
 */
public class AsyncChunkLoadUtil extends BukkitRunnable {
    private final World world;
    private final List<ChunkCoordinate> chunks;
    private final List<CompletableFuture<Chunk>> futures = new LinkedList<>();
    private int runCount = 0;

    /**
     * Creates a new loader.
     *
     * @param world the target world
     * @param chunks a list of chunk coordinates
     */
    public AsyncChunkLoadUtil(@NotNull World world, @NotNull List<ChunkCoordinate> chunks) {
        this.world = world;
        this.chunks = chunks;
    }

    @Override
    public void run() {
        if (runCount++ == 0) {
            // On first tick, ask for all chunks to be loaded
            chunks.forEach(c -> futures.add(world.getChunkAtAsync(c.x(), c.z())));
        } else {
            // On subsequent ticks, process all completed futures
            synchronized (futures) {
                final var iterator = futures.listIterator();
                while (iterator.hasNext()) {
                    final var future = iterator.next();
                    if (future.isDone()) {
                        // add ticket
                        future.thenAccept(c -> c.addPluginChunkTicket(Survive.getInstance()));
                        iterator.remove();
                    }
                }
                // Cancel once all futures are processed
                if (futures.isEmpty()) {
                    if (!isCancelled()) cancel();
                }
            }
        }
    }

    /**
     * Checks whether this loader has finished loading all chunks.
     *
     * @return true if all chunks have been loaded
     */
    public boolean isComplete() {
        synchronized (futures) {
            return futures.isEmpty();
        }
    }
}
