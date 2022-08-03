package dev.dpvb.survival.game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameListener implements Listener {

    private GameManager manager;

    public GameListener(GameManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (manager.playerInGame(player)) {
            manager.leave(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Check if player died while in game.
        Player victim = event.getPlayer();
        if (!manager.playerInGame(victim)) {
            return;
        }

        // Give tokens or whatever the heck
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (manager.playerInGame(player)) {
            manager.leave(player);
        }

        event.setRespawnLocation(manager.hubWorld.getSpawnLocation());
    }

}
