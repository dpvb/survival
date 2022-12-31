package dev.dpvb.survive.util.computation;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Calculates a bounded, count-dependent random number.
 * <p>
 * Accepts an input value {@code count} and generate random values.
 * <p>
 * The formula is {@code rng(ceil - floor) + floor - count * scale}.
 *
 * @author ms5984
 */
public final class BoundedRngCalculator {
    final int ceil;
    final int floor;
    final double scale;

    /**
     * Creates a new calculator.
     *
     * @param ceil the maximum value
     * @param floor the minimum value
     * @param scale how rapidly the maximum value decreases with count
     * @throws IllegalArgumentException if {@code floor <= 0} or
     * {@code ceil <= floor}
     */
    public BoundedRngCalculator(int ceil, int floor, double scale) throws IllegalArgumentException {
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
     * <p>
     * The section must have the following format:
     * <pre>{@code
     * ceil: # maximum (int)
     * floor: # minimum (int)
     * scale: # how rapidly the maximum decreases with input count (double)
     * }</pre>
     *
     * @param section a config section
     * @see #BoundedRngCalculator(int, int, double)
     */
    public BoundedRngCalculator(@NotNull ConfigurationSection section) throws IllegalArgumentException {
        this(
                section.getInt("ceil"),
                section.getInt("floor"),
                section.getDouble("scale")
        );
    }

    /**
     * Calculates the next value.
     *
     * @param count a count
     * @return a count-dependent, bounded random number
     */
    public int calculate(int count) {
        return (int) (ThreadLocalRandom.current().nextInt(ceil - floor) + floor - count * scale);
    }
}
