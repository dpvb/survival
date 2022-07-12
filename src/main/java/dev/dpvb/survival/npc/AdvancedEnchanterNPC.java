package dev.dpvb.survival.npc;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

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

    }
}
