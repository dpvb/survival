package dev.dpvb.survive.events;

import dev.dpvb.survive.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpawnPlayerOnJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(GameManager.getInstance().getHubWorld().getSpawnLocation());
    }

}
