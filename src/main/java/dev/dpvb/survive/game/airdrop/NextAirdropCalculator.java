package dev.dpvb.survive.game.airdrop;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.max;
import static java.lang.Math.pow;

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
    final int fuzz;

    /**
     * Creates a new calculator.
     *
     * @param ceil the maximum average time to the next drop
     * @param floor the minimum average time to the next drop
     * @param scale how rapidly the average time decreases with player count
     * @param fuzz by how much to randomize the time (must be {@code > 0})
     * @throws IllegalArgumentException if {@code fuzz < 0},
     * {@code ceil <= floor} or {@code floor <= fuzz}
     */
    NextAirdropCalculator(int ceil, int floor, double scale, int fuzz) throws IllegalArgumentException {
        if (fuzz < 0) {
            throw new IllegalArgumentException("fuzz must be greater than or equal to 0");
        }
        if (ceil <= floor) {
            throw new IllegalArgumentException("ceil must be greater than floor");
        }
        if (floor <= fuzz) {
            throw new IllegalArgumentException("floor must be greater than fuzz");
        }
        this.ceil = ceil;
        this.floor = floor;
        this.scale = scale;
        this.fuzz = fuzz;
    }

    /**
     * Loads a calculator from a config section.
     *
     * @param section a config section
     * @see #NextAirdropCalculator(int, int, double, int)
     */
    NextAirdropCalculator(@NotNull ConfigurationSection section) throws IllegalArgumentException {
        this(
                section.getInt("ceil"),
                section.getInt("floor"),
                section.getDouble("scale"),
                section.getInt("fuzz")
        );
    }

    /**
     * Calculates the time till the next airdrop in minutes.
     *
     * @param playerCount the current arena player count
     * @return the next time-till-the-next-airdrop in minutes
     */
    public int calculate(int playerCount) {
        return max((int) (ceil - pow(scale, playerCount - 1) - 1), floor) * ThreadLocalRandom.current().nextInt(-fuzz, fuzz) + 1;
    }
}
