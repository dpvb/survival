package dev.dpvb.survival.game;

import dev.dpvb.survival.chests.airdrop.AirdropManager;
import dev.dpvb.survival.chests.tiered.ChestManager;
import dev.dpvb.survival.stats.PlayerInfoManager;
import dev.dpvb.survival.util.messages.Messages;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;

public class GameListener implements Listener {

    private final GameManager manager;

    public GameListener(GameManager manager) {
        this.manager = manager;
    }

    // Handle loot chests (route player to virtual inventory)
    @EventHandler
    public void onLootChestOpen(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        final var block = event.getClickedBlock();
        //noinspection ConstantConditions (We know there is a block)
        switch (block.getType()) {
            case CHEST, ENDER_CHEST -> {
                if (block.getWorld() != manager.getArenaWorld()) return;
                final var lootChest = ChestManager.getInstance().getLootChestMap().get(block.getLocation());
                if (lootChest != null) {
                    event.setCancelled(true);
                    lootChest.open(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onInteractTrapdoor(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!manager.playerInGame(event.getPlayer())) return;
        final var type = event.getClickedBlock().getType();
        final String[] s = type.name().split("_");
        if (s[s.length - 1].equals("TRAPDOOR")) {
            event.setCancelled(true);
        }
    }

    // Handling Player Death
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
            event.setRespawnLocation(manager.getHubWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerRightClickAirdropItem(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        final var player = event.getPlayer();
        final var item = event.getItem();
        if (item == null) return;
        if (!item.hasItemMeta()) return;
        if (!manager.playerInGame(player)) return;
        final var clickedBlock = event.getClickedBlock();
        //noinspection ConstantConditions (We know there is a block)
        if (!manager.getArenaWorld().getHighestBlockAt(clickedBlock.getLocation()).equals(clickedBlock)) {
            Messages.AIRDROP_INCORRECT_PLACEMENT.send(player);
            return;
        }
        if (item.isSimilar(AirdropManager.getInstance().getAirdropItem())) {
            // Remove one airdrop item.
            final var amount = item.getAmount();
            if (amount > 1) {
                player.getInventory().getItemInMainHand().setAmount(amount - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }

            // Start Airdrop.
            AirdropManager.getInstance().startAirdrop(clickedBlock.getLocation().add(0, 1, 0));
        }
    }

    // Always prevent block places and block breaks for hub and arena worlds.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("survival.bypass")) {
            return;
        }
        final var world = event.getBlock().getWorld();
        if (world == manager.getHubWorld() || world == manager.getArenaWorld()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("survival.bypass")) {
            return;
        }
        final var world = event.getBlock().getWorld();
        if (world == manager.getHubWorld() || world == manager.getArenaWorld()) {
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
            TNTPrimed tnt = manager.getArenaWorld().spawn(event.getBlock().getLocation().add(0.5d, 0d, 0.5d), TNTPrimed.class);
            tnt.setFuseTicks(50);
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) return;
        // Make it so no blocks are destroyed
        event.blockList().clear();
    }

    // Allow gamers to use flint and steel in the arena.
    @EventHandler
    public void onFlintAndSteelUse(BlockPlaceEvent event) {
        final var player = event.getPlayer();
        if (!manager.playerInGame(player)) return;
        if (event.getItemInHand().getType() == Material.FLINT_AND_STEEL && event.isCancelled()) {
            // Re-allow place
            event.setCancelled(false);
        }
    }

    // Allow gamers to extinguish fire in the arena.
    @EventHandler
    public void onFireExtinguish(BlockBreakEvent event) {
        final var player = event.getPlayer();
        if (!manager.playerInGame(player)) return;
        if (event.getBlock().getType() == Material.FIRE && event.isCancelled()) {
            // Re-allow "break"
            event.setCancelled(false);
        }
    }

}
