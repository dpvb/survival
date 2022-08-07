package dev.dpvb.survival.npc.storage.menus;

import dev.dpvb.survival.gui.AutoCleanInventoryWrapper;
import dev.dpvb.survival.npc.storage.StorageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class StorageMenu extends AutoCleanInventoryWrapper {

    private final Player player;

    public StorageMenu(Player player) {
        this.player = player;
    }

    @Override
    protected Inventory generateInventory() {
        // Initialize the inventory
        Inventory inventory = Bukkit.createInventory(player, 54, Component.text("Storage Box"));

        // Fill contents of inventory
        Map<Integer, byte[]> contents = StorageManager.getInstance().getStorageContents(player.getUniqueId());
        for (int i = 0; i < contents.size(); i++) {
            byte[] serializedItem = contents.get(i);
            if (serializedItem == null) {
                continue;
            }
            inventory.setItem(i, ItemStack.deserializeBytes(serializedItem));
        }
        return inventory;
    }

    @Override
    public void handle(InventoryCloseEvent event) {
        Map<Integer, byte[]> contents = StorageManager.getInstance().getStorageContents(player.getUniqueId());
        for (int i = 0; i < getInventory().getSize(); i++) {
            ItemStack item = getInventory().getItem(i);
            if (item == null) {
                contents.put(i, null);
            } else {
                contents.put(i, item.serializeAsBytes());
            }
        }

        StorageManager.getInstance().updateStorageContents(player.getUniqueId(), contents);

    }
}
