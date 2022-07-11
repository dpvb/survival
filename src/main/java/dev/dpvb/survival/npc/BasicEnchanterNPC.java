package dev.dpvb.survival.npc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BasicEnchanterNPC extends AbstractNPC{

    public BasicEnchanterNPC(Location location) {
        super(
                "[NPC] b-ench",
                ChatColor.translateAlternateColorCodes('&', "&d&lBasic Enchanter"),
                "basic-enchanter",
                location
        );
    }

    @Override
    public void rightClickAction(Player clicker) {
        clicker.sendMessage("You right clicked me!");
    }
}
