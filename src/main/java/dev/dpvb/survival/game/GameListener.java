package dev.dpvb.survival.game;

import dev.dpvb.survival.stats.PlayerInfoManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

    // Always prevent block places and block breaks for hub and arena worlds.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("survival.bypass")) {
            return;
        }
        final var world = event.getBlock().getWorld();
        if (world == manager.hubWorld || world == manager.arenaWorld) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("survival.bypass")) {
            return;
        }
        final var world = event.getBlock().getWorld();
        if (world == manager.hubWorld || world == manager.arenaWorld) {
            event.setCancelled(true);
        }
    }

    // Allow gamers to place TNT in the arena.
    @EventHandler
    public void onTntWouldPlace(BlockPlaceEvent event) {
        final var player = event.getPlayer();
        if (!manager.playerInGame(player)) return;
        if (event.getBlockPlaced().getType() == Material.TNT && event.isCancelled()) {
            // Re-allow place
            event.setCancelled(false);
        }
    }

    // Change placement into "placement" (spawning of TNTPrimed)
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onTntPlacement(BlockPlaceEvent event) {
        final var player = event.getPlayer();
        if (!manager.playerInGame(player)) return;
        if (event.getBlockPlaced().getType() == Material.TNT) {
            // Prevent actual placement (still use item)
            event.getBlockReplacedState().update(true);
            // spawn TNTPrimed
            TNTPrimed tnt = manager.getArenaWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
            tnt.setFuseTicks(50);
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) return;
        // Make it so no blocks are destroyed
        event.blockList().clear();
    }

}
