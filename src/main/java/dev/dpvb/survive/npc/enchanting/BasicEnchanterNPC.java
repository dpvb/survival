package dev.dpvb.survive.npc.enchanting;

import dev.dpvb.survive.gui.InventoryWrapper;
import dev.dpvb.survive.npc.AbstractNPC;
import dev.dpvb.survive.npc.NPCManager;
import dev.dpvb.survive.npc.enchanting.menus.EnchantingInputMenu;
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
