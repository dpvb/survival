package dev.dpvb.survival.chests;


import dev.dpvb.survival.gui.InventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LootChestInventory extends InventoryWrapper {

    private LootChest chest;
    private int size;

    public LootChestInventory(LootChest chest) {
        this.chest = chest;
        this.size = 9;
    }

    @Override
    protected Inventory generateInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9);
        inventory.setItem(new Random().nextInt(size), new ItemStack(Material.STICK));
        return inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        if (event.getClickedInventory() == getInventory()) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }

            getInventory().setItem(event.getSlot(), null);
            givePlayerItem(event.getWhoClicked(), clickedItem);
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public void handle(InventoryCloseEvent event) {
        Bukkit.getLogger().info("" + event.getInventory().getViewers().size());
        if (event.getInventory().getViewers().size() == 1) {
            BlockState state = chest.getBlock().getState();
            if (state instanceof Lidded lidded) {
                lidded.close();
            }
        }
    }

    private void givePlayerItem(HumanEntity player, ItemStack item) {
        if (!player.getInventory().addItem(item).isEmpty()) {
            Location l = player.getLocation();
            l.getWorld().dropItemNaturally(l, item);
        }
    }
}
