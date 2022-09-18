package dev.dpvb.survive.game.world.util;

import dev.dpvb.survive.Survive;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsyncChunkLoadUtil extends BukkitRunnable {
    private final World world;
    private final List<ChunkCoordinate> chunks;
    private final List<CompletableFuture<Chunk>> futures = new LinkedList<>();
    private int runCount = 0;

    public AsyncChunkLoadUtil(World world, List<ChunkCoordinate> chunks) {
        this.world = world;
        this.chunks = chunks;
    }

    @Override
    public void run() {
        if (runCount++ == 0) {
            chunks.forEach(c -> futures.add(world.getChunkAtAsync(c.x(), c.z())));
        } else {
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
                if (futures.isEmpty()) {
                    if (!isCancelled()) cancel();
                }
            }
        }
    }

    public boolean isComplete() {
        synchronized (futures) {
            return futures.isEmpty();
        }
    }
}
