package dev.dpvb.survival.npc.enchanting;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.npc.AbstractNPC;
import dev.dpvb.survival.npc.NPCManager;
import dev.dpvb.survival.npc.enchanting.menus.EnchantingInputMenu;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class BasicEnchanterNPC extends AbstractNPC {

    public BasicEnchanterNPC(Location location) {
        super(
                "b-ench " + new Random().nextInt(10000),
                ChatColor.translateAlternateColorCodes('&', "&d&lBasic Enchanter"),
                "basic-enchanter",
                location
        );
    }

    public BasicEnchanterNPC(NPC npc) {
        super(npc);
    }

    @Override
    public void rightClickAction(Player clicker) {
        InventoryWrapper inputMenu = new EnchantingInputMenu(clicker, NPCManager.getInstance().getBasicEnchantments()).register();
        clicker.openInventory(inputMenu.getInventory());
    }
}
