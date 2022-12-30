package dev.dpvb.survive.chests.hackable;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.chests.LootSource;
import dev.dpvb.survive.chests.LootableChest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class HackableChest extends LootableChest implements Listener {

    public HackableChest(@NotNull Block block) {
        super(block, Material.ENDER_CHEST, BlockFace.NORTH);
        spawnChest();
        Bukkit.getPluginManager().registerEvents(this, Survive.getInstance());
    }

    @Override
    public void destroy() {
        super.destroy();
        HandlerList.unregisterAll(this);
        if (!HackableChestManager.getInstance().isClearing()) {
            HackableChestManager.getInstance().removeHackableChestFromCache(this);
        }
    }

    @EventHandler
    public void onHackableChestOpen(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!block.equals(event.getClickedBlock())) {
            return;
        }

        event.setCancelled(true);
        open(event.getPlayer());
    }

    @Override
    public @NotNull LootSource getLootSource() {
        return HackableChestManager.getInstance();
    }

    @Override
    protected String hologramText() {
        return ChatColor.RED + "LOCKED CRATE";
    }

}
