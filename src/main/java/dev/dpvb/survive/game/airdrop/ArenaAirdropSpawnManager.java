package dev.dpvb.survive.game.airdrop;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.game.tasks.NaturalAirdropSpawn;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Manages natural airdrop spawning in the arena world.
 *
 * @author ms5984
 */
public class ArenaAirdropSpawnManager {
    final NextAirdropCalculator calculator;
    final ProtoLocationGenerator locationGenerator;
    private NaturalAirdropSpawn current;
    private boolean active = false;
    private GameManager manager;

    public ArenaAirdropSpawnManager(GameManager manager) {
        this.manager = manager;
        final var frequencySection = Survive.Configuration.getAirdropSpawningSection().getConfigurationSection("frequency");
        if (frequencySection == null) {
            throw new IllegalStateException("Missing frequency subsection");
        }
        this.calculator = new NextAirdropCalculator(frequencySection);
        this.locationGenerator = new ProtoLocationGenerator(manager.getSpawnLocationsCopy());
    }

    /**
     * Starts an Airdrop Task that will spawn in the map based on a time provided by the NextAirdropCalculator.
     * 
     * @see NextAirdropCalculator#calculate(int)
     */
    public void startAirdropTask() {
        if (active) {
            return;
        }

        active = true;
        current = new NaturalAirdropSpawn(manager);
        current.runTaskLater(Survive.getInstance(), 20L * calculator.calculate(manager.getPlayerCount()));
    }

    /**
     * Gets the time-to-next-drop calculator util.
     *
     * @return the calculator
     */
    public NextAirdropCalculator getCalculator() {
        return calculator;
    }

    /**
     * Gets the airdrop proto location generator util.
     *
     * @return the proto location generator
     */
    public ProtoLocationGenerator getLocationGenerator() {
        return locationGenerator;
    }

    /**
     * Tests spawn location fitness--whether it is far enough from a list of players.
     *
     * @param players a list of vectors (player locations)
     * @param location the location to test
     * @param minDistance the minimum distance required
     * @return true if the location is far enough from all players
     */
    public static boolean testLocationFitness(@NotNull List<Vector> players, @NotNull Location location, double minDistance) {
        final var loc = location.toVector();
        for (Vector player : players) {
            if (player.distance(loc) < minDistance) {
                return false;
            }
        }
        return true;
    }
}
