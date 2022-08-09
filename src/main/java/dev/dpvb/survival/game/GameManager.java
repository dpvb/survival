package dev.dpvb.survival.game;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.chests.airdrop.AirdropManager;
import dev.dpvb.survival.chests.tiered.ChestManager;
import dev.dpvb.survival.game.extraction.Extraction;
import dev.dpvb.survival.game.tasks.ClearDrops;
import dev.dpvb.survival.mongo.MongoManager;
import dev.dpvb.survival.mongo.models.Region;
import dev.dpvb.survival.mongo.models.SpawnLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameManager {

    private static GameManager instance;
    private final Set<Player> players = new HashSet<>();
    private final Set<Extraction> extractions = new HashSet<>();
    private final List<Location> spawnLocations = new ArrayList<>();
    private final AtomicBoolean state = new AtomicBoolean();
    private final GameListener listener;
    private final World hubWorld;
    private final World arenaWorld;
    private Extraction.PollingTask extractionPoller;
    private ClearDrops clearDropsTask;

    private GameManager(World hubWorld, World arenaWorld) {
        this.hubWorld = hubWorld;
        this.arenaWorld = arenaWorld;
        listener = new GameListener(this);
    }

    /**
     * Load data and start the game.
     *
     * @throws IllegalStateException if the game is already started.
     */
    public void start() throws IllegalStateException {
        synchronized (state) {
            if (state.get()) throw new IllegalStateException("Game is running");

            // Load all Chest Data
            ChestManager.getInstance().loadLootChests();

            // Load the Extractions
            loadExtractions();

            // Initialize and start the extraction poller
            extractionPoller = new Extraction.PollingTask(this);
            extractionPoller.runTaskTimer(Survival.getInstance(), 0L, Extraction.getPollingRate());

            // Load the Spawns
            loadSpawns();

            // Initialize Tasks
            initTasks();

            // Register Listener
            Bukkit.getPluginManager().registerEvents(listener, Survival.getInstance());

            // Set state
            state.set(true);
        }
    }

    /**
     * Stop the game.
     *
     * @implNote This method will no-op if the game is not running.
     */
    public void stop() {
        synchronized (state) {
            // no-op
            if (!state.get()) return;
            // Remove all Players from the Arena (free extraction, basically)
            removeAllPlayers(false);

            // Clear chest data
            ChestManager.getInstance().getLootChestMap().clear();

            // Stop polling and clear task instance; unregister listener
            extractionPoller.cancel();
            extractionPoller = null;
            HandlerList.unregisterAll(listener);

            // Reset game data
            extractions.clear();
            spawnLocations.clear();

            // Remove Airdrops from the Arena
            AirdropManager.getInstance().clearAirdrops();

            // Cleanup Tasks
            cleanupTasks();

            // Set state
            state.set(false);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isRunning() {
        synchronized (state) {
            return state.get();
        }
    }

    /**
     * Make a player join the game.
     *
     * @param player a player
     * @return true unless the player is already in the game
     * @throws IllegalStateException if the game is not running
     */
    public boolean join(@NotNull Player player) throws IllegalStateException {
        if (!state.get()) {
            throw new IllegalStateException("Game is not running");
        }
        synchronized (players) {
            // Add player to the players set
            final boolean added = players.add(player);
            if (added) {
                // Teleport them to a random spawn location
                spawnPlayer(player);
                // Log the join
                Bukkit.getLogger().info(player.getName() + " joined the arena.");
                Bukkit.getLogger().info("Player count: " + players.size());
            }
            return added;
        }
    }

    void spawnPlayer(Player player) {
        // Get the spawn location for the Player to spawn at.
        final Location spawnLocation = spawnLocations.get(ThreadLocalRandom.current().nextInt(spawnLocations.size()));
        // Move the player to the spawn location
        player.teleport(spawnLocation);
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
            // fix bug where items are dropped at nonsense coordinates in the arena world (wrong world context)
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        player.getInventory().clear();
    }

    public void sendToHub(Player player) {
        // Get the player out of the arena and back to the hub.
        player.teleport(hubWorld.getSpawnLocation());
    }

    public void remove(@NotNull Player player) {
        synchronized (players) {
            // Remove player from the players set
            if (players.remove(player)) {
                // Log the leave
                Bukkit.getLogger().info(player.getName() + " left the arena.");
                Bukkit.getLogger().info("Player count: " + players.size());
            }
        }
    }

    /**
     * Load Extractions from MongoDB
     */
    private void loadExtractions() {
        if (state.get()) {
            throw new IllegalStateException("Game is currently running");
        }
        final List<Region> regions = MongoManager.getInstance().getExtractionRegionService().getAll();
        for (Region region : regions) {
            extractions.add(new Extraction(
                    this,
                    new Vector(region.getX1(), region.getY1(), region.getZ1()),
                    new Vector(region.getX2(), region.getY2(), region.getZ2())
            ));
        }

        Bukkit.getLogger().info("Loaded " + extractions.size() + " extraction points in the arena.");
    }

    /**
     * Load Spawns from MongoDB
     */
    private void loadSpawns() {
        if (state.get()) {
            throw new IllegalStateException("Game is currently running");
        }
        final List<SpawnLocation> spawns = MongoManager.getInstance().getSpawnLocationService().getAll();
        for (SpawnLocation spawn : spawns) {
            spawnLocations.add(new Location(
                    arenaWorld,
                    spawn.getX(),
                    spawn.getY(),
                    spawn.getZ()
            ));
        }

        Bukkit.getLogger().info("Loaded " + spawnLocations.size() + " spawn points in the arena.");
    }

    public void removeAllPlayers(boolean clearInventory) {
        for (Player player : Set.copyOf(players)) {
            if (clearInventory) dropAndClearInventory(player);
            sendToHub(player);
            remove(player);
        }
    }

    public void clearDropsOnGround() {
        Collection<Item> items = arenaWorld.getEntitiesByClass(Item.class);
        for (Item item : items) {
            item.remove();
        }
        Bukkit.getLogger().info("Removed " + items.size() + " item drops from the arena.");
    }

    public void broadcast(Component message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    private void initTasks() {
        clearDropsTask = new ClearDrops(this);
        clearDropsTask.runTaskTimer(Survival.getInstance(), 20L * 1800, 20L * 1800);
    }

    private void cleanupTasks() {
        clearDropsTask.cancel();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            final World hubWorld, arenaWorld;
            try {
                // Get the World instances.
                hubWorld = Bukkit.getWorld("hub");
                if (hubWorld == null) {
                    throw new IllegalStateException("Survival requires a world named 'hub'");
                }
                arenaWorld = Bukkit.getWorld("arena");
                if (arenaWorld == null) {
                    throw new IllegalStateException("Survival requires a world named 'arena'");
                }
            } catch (IllegalStateException e) {
                // explain what's happened
                Survival.getInstance().getLogger().severe("Failed to initialize GameManager:");
                throw e; // Bukkit will disable the plugin and log the error message we provided
            }
            instance = new GameManager(hubWorld, arenaWorld);
        }

        return instance;
    }

    public Set<Player> getPlayers() {
        // prevent modification of the set
        return Collections.unmodifiableSet(players);
    }

    public Set<Extraction> getExtractions() {
        // prevent modification of the set
        return Collections.unmodifiableSet(extractions);
    }

    public World getHubWorld() {
        return hubWorld;
    }

    public World getArenaWorld() {
        return arenaWorld;
    }
}
