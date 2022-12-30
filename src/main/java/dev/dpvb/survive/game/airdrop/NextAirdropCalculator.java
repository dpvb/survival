package dev.dpvb.survive.game.airdrop;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Calculates time till the next airdrop spawn in minutes.
 * <p>
 * Uses config and current player count to calculate the time.
 *
 * @author ms5984
 */
public final class NextAirdropCalculator {
    final int ceil;
    final int floor;
    final double scale;

    /**
     * Creates a new calculator.
     *
     * @param ceil the maximum average time to the next drop
     * @param floor the minimum average time to the next drop
     * @param scale how rapidly the average time decreases with player count
     * @throws IllegalArgumentException if {@code floor <= 0} or
     * {@code ceil <= floor}
     */
    NextAirdropCalculator(int ceil, int floor, double scale) throws IllegalArgumentException {
        if (ceil <= floor) {
            throw new IllegalArgumentException("ceil must be greater than floor");
        }
        if (floor <= 0) {
            throw new IllegalArgumentException("floor must be greater than 0");
        }
        this.ceil = ceil;
        this.floor = floor;
        this.scale = scale;
    }

    /**
     * Loads a calculator from a config section.
     *
     * @param section a config section
     * @see #NextAirdropCalculator(int, int, double)
     */
    NextAirdropCalculator(@NotNull ConfigurationSection section) throws IllegalArgumentException {
        this(
                section.getInt("ceil"),
                section.getInt("floor"),
                section.getDouble("scale")
        );
    }

    /**
     * Calculates the time till the next airdrop in minutes.
     *
     * @param playerCount the current arena player count
     * @return the next time-till-the-next-airdrop in minutes
     */
    public int calculate(int playerCount) {
        return (int) (ThreadLocalRandom.current().nextInt(ceil - floor) + floor - playerCount * scale);
    }
}
