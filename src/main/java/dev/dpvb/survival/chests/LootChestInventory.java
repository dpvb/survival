package dev.dpvb.survival.chests;

import dev.dpvb.survival.gui.InventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LootChestInventory extends InventoryWrapper {

    @Override
    protected Inventory generateInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9);
        inventory.setItem(new Random().nextInt(9), new ItemStack(Material.STICK));

        return inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {

    }

}
