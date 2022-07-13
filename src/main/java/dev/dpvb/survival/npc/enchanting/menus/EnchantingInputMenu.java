package dev.dpvb.survival.npc.enchanting.menus;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.npc.enchanting.EnchantmentCost;
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

import java.util.Map;
import java.util.Set;

public class EnchantingInputMenu extends InventoryWrapper {

    private ItemGenerator confirmGenerator;
    private final Player player;
    private ItemStack enchantingItem;
    private Map<ItemTypes, Set<EnchantmentCost>> enchantments;

    public EnchantingInputMenu(Player player, Map<ItemTypes, Set<EnchantmentCost>> enchantments) {
        this.player = player;
        this.enchantments = enchantments;
    }

    @Override
    public Inventory generateInventory() {
        // Create Inventory object
        Inventory inventory = Bukkit.createInventory(player, 27, Component.text("Enchant an Item"));

        // Initial Setup
        ItemStack backgroundItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        // Add Background
        for (int i = 0; i < 27; i++) {
            if (i == 13) {
                continue;
            }
            inventory.setItem(i, backgroundItem);
        }

        // Create Confirm Button
        confirmGenerator = new ItemGenerator()
                .setDisplayName(Component.text("Give an Item").color(NamedTextColor.RED));
        inventory.setItem(26, confirmGenerator.build(Material.RED_STAINED_GLASS_PANE));
        return inventory;
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

                    // Open the next inventory screen.
                    InventoryWrapper selectionMenu = new EnchantingSelectionMenu(player, enchantingItem, enchantments).register();
                    player.openInventory(selectionMenu.getInventory());
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
        ItemStack item;
        if (enabled) {
            item = confirmGenerator
                    .setDisplayName(Component.text("Proceed").color(NamedTextColor.GREEN))
                    .build(Material.LIME_STAINED_GLASS_PANE);
        } else {
            item = confirmGenerator
                    .setDisplayName(Component.text("Give an Item").color(NamedTextColor.RED))
                    .build(Material.RED_STAINED_GLASS_PANE);
        }
        getInventory().setItem(26, item);
    }
}
