package dev.dpvb.survive.npc.tokentrader.menus;

import dev.dpvb.survive.chests.airdrop.AirdropManager;
import dev.dpvb.survive.gui.AutoCleanInventoryWrapper;
import dev.dpvb.survive.npc.NPCManager;
import dev.dpvb.survive.npc.tokentrader.TokenTraderShopItem;
import dev.dpvb.survive.stats.PlayerInfoManager;
import dev.dpvb.survive.util.item.ItemGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TokenTraderMenu extends AutoCleanInventoryWrapper {

    private final Player player;
    private final List<TokenTraderShopItem> items;
    private final PlayerInfoManager pim;

    public TokenTraderMenu(Player player) {
        this.player = player;
        items = NPCManager.getInstance().getTokenTraderItems();
        pim = PlayerInfoManager.getInstance();
    }

    @Override
    protected Inventory generateInventory() {
        // Create inventory
        Inventory inventory = Bukkit.createInventory(player, 9, Component.text("Token Trader"));

        // Add Shop Items to Inventory
        final int tokens = pim.getTokens(player.getUniqueId());

        for (int i = 0; i < items.size(); i++) {
            final TokenTraderShopItem item = items.get(i);
            final String displayName = item.shopItemName().toUpperCase();
            final int toPlace = i + 4 - ((items.size() - 1) / 2);
            final TextColor priceColor = item.cost() > tokens ? NamedTextColor.RED : NamedTextColor.GREEN;

            inventory.setItem(toPlace, new ItemGenerator()
                    .setDisplayName(Component.text(displayName).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .addLoreLine(Component.text("Price: " + item.cost()).color(priceColor))
                    .build(item.displayMaterial())
            );
        }

        // Fill inventory.
        ItemStack backgroundItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i) != null) continue;
            inventory.setItem(i, backgroundItem);
        }

        return inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        // Disable clicks in the Player's inventory.
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() == getInventory()) {
            // Cancel if they clicked on a background item.
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                // This should not happen but if it does, lets just cancel and return.
                event.setCancelled(true);
                return;
            }
            if (clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                // They clicked on a background glass pane, just cancel and return.
                event.setCancelled(true);
                return;
            }

            // They must have clicked on one of the Shop Items. Let's check which one.
            for (TokenTraderShopItem item : items) {
                if (clickedItem.getType() == item.displayMaterial()) {
                    // Check if they can afford the item.
                    if (item.cost() > pim.getTokens(player.getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }

                    // Perform Action based on the type.
                    //noinspection SwitchStatementWithTooFewBranches - left open for expansion
                    switch (item.shopItemName()) {
                        case "airdrop" -> givePlayerItem(player, AirdropManager.getInstance().getAirdropItem());
                    }

                    // Deduct Tokens
                    pim.addTokens(player.getUniqueId(), -item.cost());

                    // Play a sound for the Player
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 1f, 1f);

                    // Close the Inventory
                    player.getInventory().close();
                }
            }
        }

        event.setCancelled(true);
    }

    private void givePlayerItem(HumanEntity player, ItemStack item) {
        if (!player.getInventory().addItem(item).isEmpty()) {
            Location l = player.getLocation();
            l.getWorld().dropItemNaturally(l, item);
        }
    }
}
