package dev.dpvb.survival.npc.tokentrader;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.npc.AbstractNPC;
import dev.dpvb.survival.npc.tokentrader.menus.TokenTraderMenu;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class TokenTraderNPC extends AbstractNPC {

    public TokenTraderNPC(Location location) {
        super(
                "t-trader " + new Random().nextInt(10000),
                ChatColor.translateAlternateColorCodes('&', "&8&lToken Trader"),
                "token-trader",
                location
        );
    }

    public TokenTraderNPC(NPC npc) {
        super(npc);
    }

    @Override
    public void rightClickAction(Player clicker) {
        InventoryWrapper menu = new TokenTraderMenu(clicker).register();
        clicker.openInventory(menu.getInventory());
    }
}
