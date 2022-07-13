package dev.dpvb.survival.npc.enchanting.menus;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.npc.enchanting.EnchantmentCost;
import dev.dpvb.survival.npc.enchanting.ItemTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class EnchantingSelectionMenu extends InventoryWrapper {

    private final Player player;
    private ItemStack item;
    private Map<ItemTypes, Set<EnchantmentCost>> enchantments;

    public EnchantingSelectionMenu(Player player, ItemStack item, Map<ItemTypes, Set<EnchantmentCost>> enchantments) {
        this.player = player;
        this.item = item;
        this.enchantments = enchantments;
    }

    @Override
    protected Inventory generateInventory() {
        // Create Inventory object
        Inventory inventory = Bukkit.createInventory(player, 27, Component.text("Apply Enchant"));

        // Initial Setup
        ItemStack backgroundItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        // Add Background
        for (int i = 0; i < 27; i++) {
            if (i >= 10 && i <= 16) {
                continue;
            }

            inventory.setItem(i, backgroundItem);
        }


        return inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {

    }
}
