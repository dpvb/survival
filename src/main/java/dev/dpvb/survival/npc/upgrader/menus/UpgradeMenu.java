package dev.dpvb.survival.npc.upgrader.menus;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.npc.NPCManager;
import dev.dpvb.survival.npc.upgrader.UpgradeCost;
import dev.dpvb.survival.stats.PlayerInfoManager;
import dev.dpvb.survival.util.item.ItemGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.internal.parser.match.TokenListProducingMatchedTokenConsumer;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.Map;

public class UpgradeMenu extends InventoryWrapper {

    private final Player player;
    private ItemGenerator upgradeButtonGenerator;
    private @Nullable ItemStack itemInSlot = null;
    private final Map<Material, UpgradeCost> upgrades;

    public UpgradeMenu(Player player) {
        this.player = player;
        this.upgrades = NPCManager.getInstance().getUpgrades();
    }

    @Override
    protected Inventory generateInventory() {
        // Create inventory
        Inventory inventory = Bukkit.createInventory(player, 27, Component.text("Upgrade your Item"));

        // Initial Setup
        ItemStack backgroundItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            if (i == 13) {
                continue;
            }

            inventory.setItem(i, backgroundItem);
        }

        // Create confirm button
        upgradeButtonGenerator = new ItemGenerator()
                .setDisplayName(Component.text("Give an Item to Upgrade").color(NamedTextColor.RED));
        inventory.setItem(26, upgradeButtonGenerator.build(Material.RED_STAINED_GLASS_PANE));
        return inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        // If Player clicks a valid Item in their inventory, swap it into the Upgrade slot.
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                int clickedSlot = event.getSlot();
                if (upgrades.containsKey(clickedItem.getType())) {
                    ItemStack itemInEnchanter = event.getInventory().getItem(13);
                    event.getClickedInventory().setItem(clickedSlot, itemInEnchanter);
                    event.getInventory().setItem(13, clickedItem);
                    itemInSlot = clickedItem;
                    updateUpgradeButton();
                }
            }
        } else if (event.getClickedInventory() == getInventory()) {
            // Check if they clicked the middle slot.
            if (event.getSlot() == 13) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null) {
                    givePlayerItem(player, clickedItem);
                    getInventory().setItem(13, null);
                    itemInSlot = null;
                    updateUpgradeButton();
                }
            }

            // Check if they clicked the Upgrade button
            if (event.getSlot() == 26) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem.getType() == Material.LIME_STAINED_GLASS_PANE) {
                    UpgradeCost upgradeCost = upgrades.get(itemInSlot.getType());

                    // Get the Item for the Player
                    itemInSlot.setType(upgradeCost.getTo());

                    // Deduct Tokens
                    PlayerInfoManager.getInstance().addTokens(player.getUniqueId(), -upgradeCost.getPrice());

                    // Play a sound for the Player
                    player.playSound(player, Sound.BLOCK_ANVIL_USE, SoundCategory.MASTER, 1f, 1f);

                    // Close the inventory
                    player.getInventory().close();

                    // Give the Player the Item
                    givePlayerItem(player, itemInSlot);
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
        if (itemInSlot == null) {
            return;
        }

        // If the inventory is full, drop the Item on the ground.
        givePlayerItem(player, itemInSlot);
    }

    private void updateUpgradeButton() {
        ItemStack item;
        if (itemInSlot == null) {
            item = upgradeButtonGenerator
                    .setDisplayName(Component.text("Give an Item to Upgrade").color(NamedTextColor.RED))
                    .build(Material.RED_STAINED_GLASS_PANE);
        } else {
            int playerTokens = PlayerInfoManager.getInstance().getTokens(player.getUniqueId());
            int price = upgrades.get(itemInSlot.getType()).getPrice();

            if (price > playerTokens) {
                item = upgradeButtonGenerator
                        .setDisplayName(Component.text("You can't afford this").color(NamedTextColor.RED))
                        .addLoreLine(Component.text("Price: " + price).color(NamedTextColor.GRAY))
                        .build(Material.RED_STAINED_GLASS_PANE);
            } else {
                item = upgradeButtonGenerator
                        .setDisplayName(Component.text("Upgrade Item").color(NamedTextColor.GREEN))
                        .addLoreLine(Component.text("Price: " + price).color(NamedTextColor.GRAY))
                        .build(Material.LIME_STAINED_GLASS_PANE);
            }
        }

        getInventory().setItem(26, item);
    }

    private void givePlayerItem(HumanEntity player, ItemStack item) {
        if (!player.getInventory().addItem(item).isEmpty()) {
            Location l = player.getLocation();
            l.getWorld().dropItemNaturally(l, item);
        }
    }
}
