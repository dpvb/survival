package dev.dpvb.survive.npc.storage;

import dev.dpvb.survive.gui.InventoryWrapper;
import dev.dpvb.survive.npc.AbstractNPC;
import dev.dpvb.survive.npc.storage.menus.StorageMenu;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class StorageNPC extends AbstractNPC {

    public StorageNPC(Location location) {
        super(
                "store " + new Random().nextInt(10000),
                ChatColor.translateAlternateColorCodes('&', "&e&lStorage Man"),
                "storage",
                location
        );
    }

    public StorageNPC(NPC npc) {
        super(npc);
    }

    @Override
    public void rightClickAction(Player clicker) {
        InventoryWrapper storage = new StorageMenu(clicker).register();
        clicker.openInventory(storage.getInventory());
    }
}
