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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnchantingSelectionMenu extends InventoryWrapper {

    private final Player player;
    private final ItemStack item;
    private final Map<ItemTypes, Set<EnchantmentCost>> enchantments;
    private Map<ItemStack, PendingEnchant> pendingEnchants;

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

        // Add Background and Item Display
        for (int i = 0; i < 27; i++) {
            if (i >= 10 && i <= 16) {
                continue;
            }

            if (i == 22) {
                inventory.setItem(i, item);
            } else {
                inventory.setItem(i, backgroundItem);
            }
        }

        // Figure out what enchantments to supply.
        List<ItemStack> enchantItems = getEnchantUpgrades();
        int enchItemsSize = enchantItems.size();
        for (int i = 0; i <= 6; i++) {
            inventory.setItem(i + 10, (i < enchItemsSize) ? enchantItems.get(i) : new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

        return inventory;
    }

    private List<ItemStack> getEnchantUpgrades() {
        Map<Enchantment, Integer> enchantsOnItem = item.getEnchantments();
        Set<EnchantmentCost> costs = enchantments.get(ItemTypes.getItemType(item.getType()));

        List<ItemStack> enchantUpgrades = new ArrayList<>();
        pendingEnchants = new HashMap<>();

        for (EnchantmentCost cost : costs) {
            Enchantment ench = cost.getEnchantment();
            int nextLevel = enchantsOnItem.getOrDefault(ench, 0) + 1;

            ItemStack itemStack;
            Component upgradeComponent = Component.text("Upgrade ").append(ench.displayName(nextLevel));

            Integer price = cost.getLevelToPrice().get(nextLevel);
            if (price != null) {
                itemStack = new ItemGenerator().setDisplayName(upgradeComponent.color(NamedTextColor.GREEN))
                        .addLoreLine(Component.text("Price: " + cost.getLevelToPrice().get(nextLevel)).color(NamedTextColor.YELLOW))
                        .build(Material.ENCHANTED_BOOK);
                pendingEnchants.put(itemStack, new PendingEnchant(cost, nextLevel));
            } else {
                itemStack = new ItemGenerator().setDisplayName(upgradeComponent.color(NamedTextColor.RED))
                        .addLoreLine(Component.text("You can't upgrade this").color(NamedTextColor.YELLOW))
                        .addLoreLine(Component.text("any further here.").color(NamedTextColor.YELLOW))
                        .build(Material.RED_STAINED_GLASS_PANE);
            }

            enchantUpgrades.add(itemStack);
        }

        return enchantUpgrades;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        if (event.getClickedInventory() == getInventory()) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.ENCHANTED_BOOK) {
                PendingEnchant pe = pendingEnchants.get(clickedItem);

                // CHECK IF PLAYER CAN AFFORD THE ENCHANTMENT.

                // Give Player the Item with the Enchantment Applied to it.
                item.addEnchantment(pe.ec.getEnchantment(), pe.level);
                player.getInventory().close();
                givePlayerItem(player, item);
            }
        }

        event.setCancelled(true);
    }

    @Override
    public void handle(InventoryCloseEvent event) {
        if (event.getReason() != InventoryCloseEvent.Reason.PLAYER) {
            return;
        }

        // Give Player Item Back if they put an Item into the Enchanter and closed it.
        givePlayerItem(player, item);
    }

    private void givePlayerItem(HumanEntity player, ItemStack item) {
        if (!player.getInventory().addItem(item).isEmpty()) {
            Location l = player.getLocation();
            l.getWorld().dropItemNaturally(l, item);
        }
    }

    private class PendingEnchant {

        protected int level;
        protected EnchantmentCost ec;

        public PendingEnchant(EnchantmentCost ec, int level) {
            this.ec = ec;
            this.level = level;
        }

    }
}
