package dev.dpvb.survival.gui;

import dev.dpvb.survival.Survival;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class InventoryWrapper implements Listener {

    protected Inventory inventory;

    protected abstract Inventory generateInventory();

    public abstract void handle(InventoryClickEvent event);

    public void handle(InventoryCloseEvent event) {

    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getInventory() == inventory) {
            handle(event);
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory() == inventory) {
            handle(event);
        }
    }

    public InventoryWrapper register() {
        Bukkit.getPluginManager().registerEvents(this, Survival.getInstance());
        return this;
    }

    public Inventory getInventory() {
        if (inventory == null) {
            inventory = generateInventory();
        }
        return inventory;
    }

}
