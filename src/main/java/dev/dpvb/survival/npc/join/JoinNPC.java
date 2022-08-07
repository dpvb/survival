package dev.dpvb.survival.npc.join;

import dev.dpvb.survival.game.GameManager;
import dev.dpvb.survival.npc.AbstractNPC;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class JoinNPC extends AbstractNPC {

    public JoinNPC(Location location) {
        super(
                "join " + new Random().nextInt(10000),
                ChatColor.translateAlternateColorCodes('&', "&a&lJoin the Arena"),
                "join",
                location
        );
    }

    public JoinNPC(NPC npc) {
        super(npc);
    }

    @Override
    public void rightClickAction(Player clicker) {
        if (!GameManager.getInstance().isRunning()) {
            clicker.sendMessage(Component.text("The game is not running right now. Please try again later."));
            return;
        }
        GameManager.getInstance().join(clicker);
    }
}
