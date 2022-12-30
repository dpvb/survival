package dev.dpvb.survive.game.tasks;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.chests.airdrop.AirdropManager;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.game.airdrop.ArenaAirdropSpawnManager;
import dev.dpvb.survive.util.messages.Message;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static dev.dpvb.survive.game.airdrop.ArenaAirdropSpawnManager.testLocationFitness;

/**
 * Spawns an airdrop at a random location in the arena.
 *
 * @author ms5984
 */
public class NaturalAirdropSpawn extends BukkitRunnable {
    private static final int MAX_TRIES = 5;
    private final ArenaAirdropSpawnManager manager;
    private final int minDistanceToNearestPlayer;
    private final List<Vector> playerLocations;
    private int runs;

    public NaturalAirdropSpawn(ArenaAirdropSpawnManager manager) {
        this.manager = manager;
        minDistanceToNearestPlayer = Survive.Configuration.getAirdropSpawningSection().getInt("min-distance-to-nearest-player");
        playerLocations = GameManager.getInstance().getPlayers().stream()
                .map(p -> p.getLocation().toVector())
                .toList();
    }

    @Override
    public void run() {
        if (!isCancelled()) {
            if (runs == 0) {
                Message.mini("Selecting an airdrop spawn location").sendConsole();
            }
            // Get a random location
            var randomLocation = manager.getLocationGenerator().getRandomLocation();
            // +1
            randomLocation.add(0d, 1d, 0d);
            if (runs++ < MAX_TRIES && manager.getLocationGenerator().getCurrentSize() > 1) {
                Message.mini("Trying location #<loc>", Placeholder.parsed("loc", String.valueOf(runs))).sendConsole();
                if (!testLocationFitness(playerLocations, randomLocation, minDistanceToNearestPlayer)) {
                    return;
                }
                Message.mini("Location #<loc> selected!", Placeholder.parsed("loc", String.valueOf(runs))).sendConsole();
            } else {
                // we failed to find a sufficiently distant location, just use last one we found
                Message.mini("Backup location selected").sendConsole();
            }
            // spawn airdrop
            Message.mini("Spawning airdrop at <loc>", Placeholder.unparsed("loc", randomLocation.toString())).sendConsole();
            AirdropManager.getInstance().startAirdrop(randomLocation);
            // schedule a repopulate task next-tick
            Bukkit.getScheduler().runTask(Survive.getInstance(), manager.getLocationGenerator()::repopulate);
            if (GameManager.getInstance().isRunning()) {
                // schedule next airdrop
                manager.scheduleNextDrop();
            }
            cancel();
        }
    }
}
