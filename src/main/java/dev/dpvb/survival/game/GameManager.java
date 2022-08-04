package dev.dpvb.survival.game;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.game.extraction.Extraction;
import dev.dpvb.survival.mongo.MongoManager;
import dev.dpvb.survival.mongo.models.ExtractionRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameManager {

    private static GameManager instance;
    final World hubWorld;
    private final World arenaWorld;
    private final Set<Player> players = new HashSet<>();
    private final Set<Extraction> extractions = new HashSet<>();
    private final Extraction.PollingTask extractionPoller;

    private GameManager() {
        // Get the World instances.
        final var hub = Bukkit.getWorld("hub");
        if (hub == null) {
            throw new IllegalStateException("Survival requires a world named 'hub'");
        } else {
            hubWorld = hub;
        }
        arenaWorld = Bukkit.getWorld("arena");
        if (arenaWorld == null) {
            throw new IllegalStateException("Survival requires a world named 'arena'");
        }

        // Load the Extractions
        loadExtractions();
        extractionPoller = new Extraction.PollingTask(this);
        extractionPoller.runTaskTimer(Survival.getInstance(), 0L, Extraction.getPollingRate());

        // Initialize Listener
        Bukkit.getPluginManager().registerEvents(new GameListener(this), Survival.getInstance());
    }

    public void join(@NotNull Player player) {
        // Get the spawn location for the Player to spawn at.
        final Location spawnLocation = arenaWorld.getSpawnLocation();
        // Move the player to the spawn location
        player.teleport(spawnLocation);
        // Add player to the players set
        players.add(player);
        // Log the join
        Bukkit.getLogger().info(player.getName() + " joined the arena.");
        Bukkit.getLogger().info("Player count: " + players.size());
    }

    public boolean playerInGame(Player player) {
        return players.contains(player);
    }

    public void dropAndClearInventory(Player player) {
        // Drop the player's inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }
            arenaWorld.dropItemNaturally(player.getLocation(), item);
        }
        player.getInventory().clear();
    }

    public void sendToHub(Player player) {
        // Get the player out of the arena and back to the hub.
        player.teleport(hubWorld.getSpawnLocation());
    }

    public void remove(@NotNull Player player) {
        // Remove player from the players set
        players.remove(player);
        // Log the leave
        Bukkit.getLogger().info(player.getName() + " left the arena.");
        Bukkit.getLogger().info("Player count: " + players.size());
    }

    /**
     * Load Extractions from MongoDB
     */
    private void loadExtractions() {
        final List<ExtractionRegion> regions = MongoManager.getInstance().getExtractionRegionService().getAll();
        for (ExtractionRegion region : regions) {
            extractions.add(new Extraction(
                    this,
                    new Vector(region.getX1(), region.getY1(), region.getZ1()),
                    new Vector(region.getX2(), region.getY2(), region.getZ2())
            ));
        }

        Bukkit.getLogger().info("Loaded " + extractions.size() + " extraction points in the arena.");
    }

    public void removeAllPlayers(boolean clearInventory) {
        for (Player player : Set.copyOf(players)) {
            if (clearInventory) dropAndClearInventory(player);
            sendToHub(player);
            remove(player);
        }
    }

    public static GameManager getInstance() {
        if (instance == null) {
            try {
                instance = new GameManager();
            } catch (IllegalStateException e) {
                // explain what's happened
                Survival.getInstance().getLogger().severe("Failed to initialize GameManager:");
                throw e; // Bukkit will disable the plugin and log the error message we provided
            }
        }

        return instance;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Set<Extraction> getExtractions() {
        return extractions;
    }

    public World getArenaWorld() {
        return arenaWorld;
    }
}
