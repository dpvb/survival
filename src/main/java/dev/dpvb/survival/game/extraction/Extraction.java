package dev.dpvb.survival.game.extraction;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class Extraction implements Listener {

    private final Location corner1;
    private final Location corner2;
    private final Vector min;
    private final Vector max;
    private final GameManager manager;
    private static final int EXTRACTION_TIME = 10;

    public Extraction(GameManager manager, Location corner1, Location corner2) {
        this.manager = manager;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.min = Vector.getMinimum(corner1.toVector(), corner2.toVector());
        this.max = Vector.getMaximum(corner1.toVector(), corner2.toVector());

        Bukkit.getPluginManager().registerEvents(this, Survival.getInstance());
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        if (!manager.playerInGame(player)) return;
        boolean isSneaking = !player.isSneaking();
        if (player.getLocation().toVector().isInAABB(min, max)) {
            Bukkit.getLogger().info(isSneaking + "");
        }
    }

}
