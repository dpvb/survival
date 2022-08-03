package dev.dpvb.survival.game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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

}
