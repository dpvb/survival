package dev.dpvb.survival.npc;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class BasicEnchanterNPC extends AbstractNPC{

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
        clicker.sendMessage("You right clicked me!");
    }
}
