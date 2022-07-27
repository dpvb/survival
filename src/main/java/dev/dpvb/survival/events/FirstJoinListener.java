package dev.dpvb.survival.events;

import dev.dpvb.survival.npc.storage.StorageManager;
import dev.dpvb.survival.stats.PlayerInfoManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class FirstJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final PlayerInfoManager pim = PlayerInfoManager.getInstance();
        final UUID uuid = event.getPlayer().getUniqueId();
        if (!pim.playerInfoExists(uuid)) {
            // If they haven't played before, create a player info entry for them!
            pim.generatePlayerInfo(uuid);
        }

        final StorageManager storageManager = StorageManager.getInstance();
        if (!storageManager.storageExists(uuid)) {
            // If they haven't played before, create a storage info entry for them!
            storageManager.generatePlayerStorage(uuid);
        }
    }

}
