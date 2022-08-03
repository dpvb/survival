package dev.dpvb.survival.game;

import dev.dpvb.survival.Survival;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class GameManager {

    private static GameManager instance;
    private final World hubWorld;
    private final World arenaWorld;
    private final Set<Player> players = new HashSet<>();

    private GameManager() {
        // Get the World instances.
        hubWorld = Bukkit.getWorld("hub");
        if (hubWorld == null) {
            Bukkit.getLogger().severe("Survival requires a world named: hub");
            Bukkit.getPluginManager().disablePlugin(Survival.getInstance());
        }
        arenaWorld = Bukkit.getWorld("arena");
        if (arenaWorld == null) {
            Bukkit.getLogger().severe("Survival requires a world named: arena");
            Bukkit.getPluginManager().disablePlugin(Survival.getInstance());
        }
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
    }

    public void leave(@NotNull Player player) {
        // Get the player out of the arena and back to the hub.
        player.teleport(hubWorld.getSpawnLocation());
        // Clear the Player's inventory.
        player.getInventory().clear();
        // Remove player from the players set
        players.remove(player);
    }

    public boolean playerInGame(Player player) {
        return players.contains(player);
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }

        return instance;
    }
}
