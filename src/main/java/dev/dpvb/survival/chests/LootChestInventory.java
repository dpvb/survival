package dev.dpvb.survival.chests;

import dev.dpvb.survival.gui.InventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lidded;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LootChestInventory extends InventoryWrapper {

    private LootChest chest;

    public LootChestInventory(LootChest chest) {
        this.chest = chest;
    }

    @Override
    protected Inventory generateInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9);
        inventory.setItem(new Random().nextInt(9), new ItemStack(Material.STICK));
        return inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {

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
}
