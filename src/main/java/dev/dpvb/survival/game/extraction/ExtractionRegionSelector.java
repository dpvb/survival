package dev.dpvb.survival.game.extraction;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.mongo.models.ExtractionRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class ExtractionRegionSelector implements Listener {

    private final Player player;
    private final Consumer<ExtractionRegion> consumer;
    private static ItemStack selectionTool;
    private static NamespacedKey key;
    private int x1;
    private int y1;
    private int z1;
    private boolean firstSelected = false;
    private int x2;
    private int y2;
    private int z2;

    public ExtractionRegionSelector(Player player, Consumer<ExtractionRegion> consumer) {
        this.player = player;
        this.consumer = consumer;
        player.getInventory().addItem(getTool());
        Bukkit.getPluginManager().registerEvents(this, Survival.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClickBlock(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player != this.player) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (!itemInMainHand.hasItemMeta()) return;
        final ItemMeta meta = itemInMainHand.getItemMeta();
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) return;
        Block b = event.getClickedBlock();
        if (!firstSelected) {
            x1 = b.getX();
            y1 = b.getY();
            z1 = b.getZ();
            firstSelected = true;
            player.sendMessage("You have set the first corner.");
        } else {
            x2 = b.getX();
            y2 = b.getY();
            z2 = b.getZ();
            player.sendMessage("You have set the second corner.");

            // Remove Item
            player.getInventory().removeItem(selectionTool);
            // Unregister this listener
            PlayerInteractEvent.getHandlerList().unregister(this);
            // Run consumer
            consumer.accept(new ExtractionRegion(x1, y1, z1, x2, y2, z2));
        }

    }

    private static ItemStack getTool() {
        if (selectionTool == null) {
            selectionTool = new ItemStack(Material.STICK);
            if (key == null) {
                key = new NamespacedKey(Survival.getInstance(), "selection-tool");
            }
            final ItemMeta meta = selectionTool.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");
            meta.displayName(Component.text("Selection Tool").decorate(TextDecoration.BOLD));
            selectionTool.setItemMeta(meta);
        }

        return selectionTool;
    }

}
