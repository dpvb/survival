package dev.dpvb.survive.game;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.chests.airdrop.AirdropManager;
import dev.dpvb.survive.chests.tiered.ChestManager;
import dev.dpvb.survive.game.extraction.Extraction;
import dev.dpvb.survive.game.tasks.ClearDrops;
import dev.dpvb.survive.game.world.ArenaChunkTicketManager;
import dev.dpvb.survive.mongo.MongoManager;
import dev.dpvb.survive.mongo.models.Region;
import dev.dpvb.survive.mongo.models.SpawnLocation;
import dev.dpvb.survive.util.messages.Messages;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class GameManager implements ForwardingAudience {

    private static GameManager instance;
    private final Set<Player> players = new HashSet<>();
    private final Set<Extraction> extractions = new HashSet<>();
    private final List<Location> spawnLocations = new ArrayList<>();
    private final AtomicBoolean state = new AtomicBoolean();
    private final ArenaChunkTicketManager arenaChunkTicketManager;
    private final GameListener listener;
    private final World hubWorld;
    private final World arenaWorld;
    private Extraction.PollingTask extractionPoller;
    private ClearDrops clearDropsTask;

    private GameManager(World hubWorld, World arenaWorld) {
        this.hubWorld = hubWorld;
        this.arenaWorld = arenaWorld;
        arenaChunkTicketManager = new ArenaChunkTicketManager(this);
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
            extractionPoller.runTaskTimer(Survive.getInstance(), 0L, Extraction.getPollingRate());

            // Load the Spawns
            loadSpawns();

            // Initialize Tasks
            initTasks();

            // Setup chunk ticket manager
            arenaChunkTicketManager.calculate();

            // Register Listener
            Bukkit.getPluginManager().registerEvents(listener, Survive.getInstance());

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

            // Remove chunk tickets
            arenaChunkTicketManager.clearTickets();

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
     * @throws IllegalStateException if the game is not running
     */
    public void join(@NotNull Player player) throws IllegalStateException {
        add(player, (added, gamer) -> {
            if (added) {
                // Teleport them to a random spawn location
                spawnPlayer(gamer);
                // Log the join
                Messages.game("log.join.standard_").replace("{player}", gamer.getName()).sendConsole();
                Messages.game("log.playerCount_").replace("{count}", "" + players.size()).sendConsole();
            } else {
                Messages.game("game.alreadyIn.player").send(gamer);
            }
        });
    }

    /**
     * Make a player leave the game.
     *
     * @param player a player
     * @param dropAndClearInventory whether to drop the player's inventory
     * @param sendToHub whether to send the player to the hub
     * @implNote This method will no-op if the game is not running or if
     * <code>player</code> was not in the game.
     */
    public void leave(@NotNull Player player, boolean dropAndClearInventory, boolean sendToHub) {
        remove(player, (removed, gamer) -> {
            if (removed) {
                if (dropAndClearInventory) dropAndClearInventory(gamer);
                if (sendToHub) sendToHub(gamer);
                // Log the leave
                Messages.game("log.leave.standard_").replace("{player}", gamer.getName()).sendConsole();
                Messages.game("log.playerCount_").replace("{count}", "" + players.size()).sendConsole();
            }
        });
    }

    /**
     * Add an admin to the game.
     *
     * @param player a player
     * @throws IllegalStateException if the game is not running
     * @implNote Logged.
     */
    public void adminJoin(@NotNull Player player) throws IllegalStateException {
        add(player, (added, gamer) -> {
            if (added) {
                Messages.game("admin.join.self").send(gamer);
                // Take them to the arena if needed
                if (gamer.getWorld() != arenaWorld) {
                    spawnPlayer(gamer);
                }
                // Log the join
                Messages.game("log.join.admin_").replace("{player}", gamer.getName()).sendConsole();
                Messages.game("log.playerCount_").replace("{count}", "" + players.size()).sendConsole();
            } else {
                Messages.game("game.alreadyIn.player").send(gamer);
            }
        });
    }

    /**
     * Remove an admin from the game.
     *
     * @param player a player
     * @implNote Logged.
     */
    public void adminLeave(@NotNull Player player) {
        remove(player, (removed, gamer) -> {
            if (removed) {
                Messages.game("admin.leave.self").send(gamer);
                // Log the leave
                Messages.game("log.leave.admin_").replace("{player}", gamer.getName()).sendConsole();
                Messages.game("log.playerCount_").replace("{count}", "" + players.size()).sendConsole();
            } else {
                Messages.game("game.notIn.player").send(gamer);
            }
        });
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

    public void sendToHub(Player player) {
        // Get the player out of the arena and back to the hub.
        player.teleport(hubWorld.getSpawnLocation());
    }

    /**
     * Add a player directly to the game.
     *
     * @param player a player
     * @param runSync a function to accept the results of the add operation
     * @throws IllegalStateException if the game is not running
     */
    public void add(@NotNull Player player, @NotNull BiConsumer<Boolean, Player> runSync) throws IllegalStateException {
        if (!state.get()) {
            throw new IllegalStateException("Game is not running");
        }
        synchronized (players) {
            runSync.accept(players.add(player), player);
            // Notify clear task
            clearDropsTask.playerSinceLastClear();
        }
    }

    /**
     * Remove a player directly from the game.
     *
     * @param player a player
     * @param runSync a function to accept the results of the remove operation
     * @implNote This method will no-op if the game is not running;
     * specifically, <code>runSync</code> will not be called.
     */
    public void remove(@NotNull Player player, @NotNull BiConsumer<Boolean, Player> runSync) {
        if (!state.get()) {
            // no-op
            return;
        }
        synchronized (players) {
            // Remove player from the players set
            runSync.accept(players.remove(player), player);
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

        Messages.Count.LOADED_EXTRACTIONS_LOG_.counted(extractions.size()).sendConsole();
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

        Messages.Count.LOADED_SPAWNS_LOG_.counted(spawnLocations.size()).sendConsole();
    }

    public void removeAllPlayers(boolean clearInventory) {
        for (Player player : Set.copyOf(players)) {
            if (clearInventory) dropAndClearInventory(player);
            sendToHub(player);
            leave(player, false, true); // free extraction
        }
    }


    /**
     * Clear drops in the arena and get the number of stacks cleared.
     *
     * @return a CompletableFuture of stacks cleared
     */
    public CompletableFuture<Integer> clearDropsOnGround() {
        // clear sync if possible
        if (arenaChunkTicketManager.isAdded()) {
            final int value = clearDrops();
            return CompletableFuture.completedFuture(value);
        }
        // load chunks (non-blocking)
        Messages.game("system.delay.loadingChunksForDropClear").sendConsole();
        final CompletableFuture<Integer> result = new CompletableFuture<>();
        arenaChunkTicketManager.addTicketsThen(() -> result.complete(clearDrops()));
        return result;
    }

    /**
     * Clears drops on ground and returns the amount cleared from the arena.
     * @return The amount of drops cleared.
     */
    private int clearDrops() {
        Collection<Item> items = arenaWorld.getEntitiesByClass(Item.class);
        for (Item item : items) {
            item.remove();
        }

        Messages.Count.CLEARED_ITEM_DROPS_LOG_.counted(items.size()).sendConsole();
        // we can let the chunks unload now
        arenaChunkTicketManager.clearTickets();
        return items.size();
    }

    private void initTasks() {
        clearDropsTask = new ClearDrops(this);
        clearDropsTask.runTaskTimer(Survive.getInstance(), 20L * 1770, 20L * 1800);
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
                    throw new IllegalStateException("Survive requires a world named 'hub'");
                }
                arenaWorld = Bukkit.getWorld("arena");
                if (arenaWorld == null) {
                    throw new IllegalStateException("Survive requires a world named 'arena'");
                }
            } catch (IllegalStateException e) {
                // explain what's happened
                Survive.getInstance().getLogger().severe("Failed to initialize GameManager:");
                throw e; // Bukkit will disable the plugin and log the error message we provided
            }
            instance = new GameManager(hubWorld, arenaWorld);
        }

        return instance;
    }

    @Override
    public @NotNull Iterable<Player> audiences() {
        return List.copyOf(players);
    }

    public Set<Player> getPlayers() {
        // prevent modification of the set
        return Collections.unmodifiableSet(players);
    }

    public Set<Extraction> getExtractions() {
        // prevent modification of the set
        return Collections.unmodifiableSet(extractions);
    }

    public ArenaChunkTicketManager getArenaChunkTicketManager() {
        return arenaChunkTicketManager;
    }

    public World getHubWorld() {
        return hubWorld;
    }

    public World getArenaWorld() {
        return arenaWorld;
    }

    public static void dropAndClearInventory(Player player) {
        // Drop the player's inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        player.getInventory().clear();
    }
}
