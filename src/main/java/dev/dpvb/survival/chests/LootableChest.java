package dev.dpvb.survival.chests;

import dev.dpvb.survival.gui.InventoryWrapper;
import org.bukkit.block.Block;
import org.bukkit.block.Lidded;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface LootableChest {
    default void open(Player player) {
        // Open the inventory for the player.
        player.openInventory(getInventoryWrapper().getInventory());

        // Visually open the block.
        final var state = getBlock().getState();
        if (state instanceof Lidded lidded) {
            if (!lidded.isOpen()) {
                lidded.open();
            }
        }
    }
    @NotNull InventoryWrapper getInventoryWrapper();
    @NotNull Block getBlock();
}
