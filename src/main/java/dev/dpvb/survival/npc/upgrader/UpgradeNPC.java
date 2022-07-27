package dev.dpvb.survival.npc.upgrader;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.npc.AbstractNPC;
import dev.dpvb.survival.npc.upgrader.menus.UpgradeMenu;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class UpgradeNPC extends AbstractNPC {

    public UpgradeNPC(Location location) {
        super(
                "upgrade " + new Random().nextInt(10000),
                ChatColor.translateAlternateColorCodes('&', "&6&lUpgrader"),
                "upgrader",
                location
        );
    }

    public UpgradeNPC(NPC npc) {
        super(npc);
    }

    @Override
    public void rightClickAction(Player clicker) {
        InventoryWrapper menu = new UpgradeMenu(clicker).register();
        clicker.openInventory(menu.getInventory());
    }
}
