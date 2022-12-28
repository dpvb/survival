package dev.dpvb.survive.game.airdrop;

import org.bukkit.Location;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Selects a random location as the base for an airdrop spawn location.
 *
 * @author ms5984
 */
public class ProtoLocationGenerator {
    private final List<Location> sources;

    ProtoLocationGenerator(List<Location> sources) {
        this.sources = sources;
    }

    /**
     * Gets a random location from the list of sources.
     *
     * @return a random location
     */
    public Location getRandomLocation() {
        return sources.get(ThreadLocalRandom.current().nextInt(sources.size()));
    }
}
