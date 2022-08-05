package dev.dpvb.survival.game;

import dev.dpvb.survival.stats.PlayerInfoManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameListener implements Listener {

    private final GameManager manager;

    public GameListener(GameManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Check if player died while in game.
        final var victim = event.getPlayer();
        if (!manager.playerInGame(victim)) {
            return;
        }

        final var infoManager = PlayerInfoManager.getInstance();
        infoManager.addDeath(victim.getUniqueId());

        final var killer = victim.getKiller();
        if (killer == null) {
            return;
        }

        infoManager.addTokens(killer.getUniqueId(), 1);
        infoManager.addKill(killer.getUniqueId());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        final var player = event.getPlayer();
        if (manager.playerInGame(player)) {
            manager.dropAndClearInventory(player);
            manager.sendToHub(player);
            manager.remove(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final var player = event.getPlayer();
        if (manager.playerInGame(player)) {
            manager.remove(player);
            event.setRespawnLocation(manager.hubWorld.getSpawnLocation());
        }
    }

    @EventHandler
    public void onTntPlace(BlockPlaceEvent event) {
        final var player = event.getPlayer();
        if (!manager.playerInGame(player)) return;
        if (event.getBlockPlaced().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            // spawn tnt
            Entity tnt = manager.getArenaWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
            ((TNTPrimed) tnt).setFuseTicks(50);
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) return;
        // Make it so no blocks are destroyed
        event.blockList().clear();
    }

}
