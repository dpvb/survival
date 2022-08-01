package dev.dpvb.survival.chests.events;

import dev.dpvb.survival.chests.ChestManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

public class ChestListener implements Listener {

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block.getType() != Material.CHEST && block.getType() != Material.ENDER_CHEST) {
            return;
        }

        event.setCancelled(true);

        // Open the LootChest
        Optional.ofNullable(ChestManager.getInstance().getLootChestMap().get(block.getLocation()))
                .ifPresent(lc -> lc.open(event.getPlayer()));


    }

}
