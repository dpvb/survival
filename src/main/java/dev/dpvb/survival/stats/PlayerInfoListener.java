package dev.dpvb.survival.stats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerInfoListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final PlayerInfoManager pim = PlayerInfoManager.getInstance();
        final UUID uuid = event.getPlayer().getUniqueId();
        if (!pim.playerInfoExists(uuid)) {
            // If they haven't played before, create a player info entry for them!
            pim.generatePlayerInfo(uuid);
        }
    }

}
