package dev.dpvb.survival.chests.airdrop;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.chests.ChestTier;
import dev.dpvb.survival.chests.LootSource;
import dev.dpvb.survival.chests.LootableChest;
import org.bukkit.Bukkit;
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

public class AirdropChest extends LootableChest implements Listener {

    public AirdropChest(@NotNull Block block) {
        super(block, Material.CHEST, BlockFace.NORTH);
        spawnChest();
        Bukkit.getPluginManager().registerEvents(this, Survival.getInstance());
    }

    @Override
    public @NotNull LootSource getLootSource() {
        return ChestTier.THREE;
    }

    @Override
    public void destroy() {
        super.destroy();
        // Unregister this custom listener
        HandlerList.unregisterAll(this);
        // Do not allow edits of cache while it is clearing
        if (!AirdropManager.getInstance().isClearing()) {
            AirdropManager.getInstance().removeAirdropFromCache(this);
        }
    }

    @EventHandler
    public void onAirdropOpen(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!block.equals(event.getClickedBlock())) {
            return;
        }

        open(event.getPlayer());
    }
}
