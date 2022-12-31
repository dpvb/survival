package dev.dpvb.survive.game.spawn;

import dev.dpvb.survive.Survive;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpawnTool implements Listener {

    private final Player player;
    private static ItemStack selectionTool;
    private static NamespacedKey key;
    private final List<Location> spawnLocations = new ArrayList<>();
    private final Consumer<List<Location>> consumer;

    public SpawnTool(Player player, Consumer<List<Location>> consumer) {
        this.player = player;
        this.consumer = consumer;
        player.getInventory().addItem(getTool());
        Bukkit.getPluginManager().registerEvents(this, Survive.getInstance());
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
        //noinspection ConstantConditions (we know there is a block)
        spawnLocations.add(event.getClickedBlock().getLocation());
        player.sendMessage("Added a spawn location.");
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        final ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem.equals(selectionTool)) {
            // Delete the Item
            event.getItemDrop().remove();

            // Do something with the locations.
            consumer.accept(spawnLocations);

            // Unregister the listener. this is embarrassing moment for us.
            HandlerList.unregisterAll(this);
        }
    }


    private static ItemStack getTool() {
        if (selectionTool == null) {
            selectionTool = new ItemStack(Material.STICK);
            if (key == null) {
                key = new NamespacedKey(Survive.getInstance(), "spawn-selection-tool");
            }
            final ItemMeta meta = selectionTool.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");
            meta.displayName(Component.text("Spawn Selection Tool").decorate(TextDecoration.BOLD));
            selectionTool.setItemMeta(meta);
        }

        return selectionTool;
    }

}
