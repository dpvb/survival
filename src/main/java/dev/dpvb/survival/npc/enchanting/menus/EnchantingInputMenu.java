package dev.dpvb.survival.npc.enchanting.menus;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.npc.enchanting.ItemTypes;
import dev.dpvb.survival.util.item.ItemGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantingInputMenu extends InventoryWrapper {

    private ItemGenerator confirmGenerator;
    private final Player player;
    private ItemStack enchantingItem;

    public EnchantingInputMenu(Player player) {
        this.player = player;
    }

    @Override
    public Inventory generateInventory() {
        // Create Inventory object
        Inventory page1 = Bukkit.createInventory(player, 27, Component.text("Enchant an Item"));

        // Initial Setup
        ItemStack backgroundItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        // Add Background
        for (int i = 0; i < 27; i++) {
            if (i == 13) {
                continue;
            }
            page1.setItem(i, backgroundItem);
        }

        // Create Confirm Button
        confirmGenerator = new ItemGenerator(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName(Component.text("Give an Item").color(NamedTextColor.RED));
        page1.setItem(26, confirmGenerator.build());
        return page1;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        // If Player clicks a valid Item in their inventory, swap it into the enchantment slot.
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                int clickedSlot = event.getSlot();
                for (ItemTypes itemType : ItemTypes.values()) {
                    if (itemType.test(clickedItem.getType())) {
                        ItemStack itemInEnchanter = event.getInventory().getItem(13);
                        event.getClickedInventory().setItem(clickedSlot, itemInEnchanter);
                        event.getInventory().setItem(13, clickedItem);
                        updateConfirmButtonMaterial(true);
                    }
                }
            }
        } else if (event.getClickedInventory() == getInventory()) {
            // Check if they clicked the middle slot.
            if (event.getSlot() == 13) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null) {
                    givePlayerItem(player, clickedItem);
                    getInventory().setItem(13, null);
                    updateConfirmButtonMaterial(false);
                }
            }

            // Check if Proceed button was clicked.
            if (event.getSlot() == 26) {
                ItemStack clickedItem = event.getCurrentItem();
                //noinspection ConstantConditions
                if (clickedItem.getType() == Material.LIME_STAINED_GLASS_PANE) {
                    enchantingItem = getInventory().getItem(13);
                    player.getInventory().close();
                }
            }
        }

        event.setCancelled(true);
    }

    @Override
    public void handle(InventoryCloseEvent event) {
        if (event.getReason() != InventoryCloseEvent.Reason.PLAYER) {
            return;
        }

        // Give Player Item back if they put an Item into the Enchanter and closed it.
        ItemStack item = event.getInventory().getItem(13);
        if (item == null) {
            return;
        }

        // If the inventory is full, drop the Item on the ground.
        givePlayerItem(player, item);
    }

    private void givePlayerItem(HumanEntity player, ItemStack item) {
        if (!player.getInventory().addItem(item).isEmpty()) {
            Location l = player.getLocation();
            l.getWorld().dropItemNaturally(l, item);
        }
    }

    private void updateConfirmButtonMaterial(boolean enabled) {
        if (enabled) {
            confirmGenerator.setMaterial(Material.LIME_STAINED_GLASS_PANE)
                    .setDisplayName(Component.text("Proceed").color(NamedTextColor.GREEN));
        } else {
            confirmGenerator.setMaterial(Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName(Component.text("Give an Item").color(NamedTextColor.RED));
        }
        getInventory().setItem(26, confirmGenerator.build());
    }
}
