package dev.dpvb.survive.game.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Selects a random location as the base for an airdrop spawn location.
 *
 * @author ms5984
 */
public class RandomLocationGenerator {
    private static final int MAX_FUZZ = 8;
    private final Supplier<List<Location>> sourcesGetter;
    private final Queue<Location> randomized;
    private final AtomicInteger position = new AtomicInteger();

    RandomLocationGenerator(Supplier<List<Location>> sources) {
        this.sourcesGetter = sources;
        this.randomized = new ConcurrentLinkedQueue<>();
    }

    /**
     * Gets a random location from the list of sources.
     *
     * @return a random location
     */
    public Location getRandomLocation() {
        synchronized (randomized) {
            return randomized.poll();
        }
    }

    /**
     * Gets the current size of the queue.
     *
     * @return the current size of the locations queue
     */
    public int getCurrentSize() {
        return randomized.size();
    }

    /**
     * Repopulates the queue of randomized locations.
     */
    public void repopulate() {
        final List<Location> sources = sourcesGetter.get();
        // calculate deficit
        final int deficit = sources.size() - randomized.size();
        if (deficit <= 0) return;
        // clone + fuzz async
        final var out = CompletableFuture.supplyAsync(() -> {
            final int sourcesSize = sources.size();
            final List<Location> fuzzed = new ArrayList<>(deficit);
            for (int i = 0; i < deficit; ++i) {
                fuzzed.add(fuzzMutateLocation(sources.get((position.getAndUpdate(v -> (v < sourcesSize) ? v + 1 : 0) + i) % sourcesSize).clone()));
            }
            return fuzzed;
        }).join();
        // fix to highest y sync
        for (Location location : out) mutateToHighestYAtXZ(location);
        // add to queue
        randomized.addAll(out);
    }

    private static Location fuzzMutateLocation(@NotNull Location location) {
        // apply random offsets to the location (x and z only)
        location.setX(location.getX() + ThreadLocalRandom.current().nextInt(MAX_FUZZ * 2) - MAX_FUZZ);
        location.setZ(location.getZ() + ThreadLocalRandom.current().nextInt(MAX_FUZZ * 2) - MAX_FUZZ);
        return location;
    }

    private static void mutateToHighestYAtXZ(@NotNull Location location) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This method must be called from the main thread");
        }
        // mutate location
        location.setY(location.getWorld().getHighestBlockYAt(location));
    }
}
