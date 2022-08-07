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

public class AdvancedEnchanterNPC extends AbstractNPC {

    public AdvancedEnchanterNPC(Location location) {
        super(
                "a-ench " + new Random().nextInt(10000),
                ChatColor.translateAlternateColorCodes('&', "&d&lAdvanced Enchanter"),
                "advanced-enchanter",
                location
        );
    }

    public AdvancedEnchanterNPC(NPC npc) {
        super(npc);
    }

    @Override
    public void rightClickAction(Player clicker) {
        InventoryWrapper inputMenu = new EnchantingInputMenu(clicker, NPCManager.getInstance().getAdvancedEnchantments()).register();
        clicker.openInventory(inputMenu.getInventory());
    }

}
