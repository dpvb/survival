package dev.dpvb.survive.game.extraction;

import dev.dpvb.survive.game.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Extraction {

    private final Vector min;
    private final Vector max;
    private final GameManager manager;
    private final Map<Player, Integer> extractionMap = new HashMap<>();
    private static final int EXTRACTION_TIME = 10;
    private static final long POLLING_RATE = 20L;

    public Extraction(GameManager manager, Vector corner1, Vector corner2) {
        this.manager = manager;
        this.min = Vector.getMinimum(corner1, corner2);
        this.max = Vector.getMaximum(corner1, corner2).add(new Vector(1, 0, 1));
    }

    private void process(Map<Player, Vector> vectorMap) {
        for (final Map.Entry<Player, Vector> entry : vectorMap.entrySet()) {
            final boolean inside = entry.getValue().isInAABB(min, max);
            // If they are not inside region, attempt to remove them from extractionMap.
            if (!inside) {
                extractionMap.remove(entry.getKey());
                continue;
            }
            final Player player = entry.getKey();
            // They are inside.
            int newTime = extractionMap.getOrDefault(player, 0) + 1;
            if (newTime == EXTRACTION_TIME) {
                manager.getArenaWorld().strikeLightningEffect(player.getLocation());
                extractionMap.remove(player);
                manager.leave(player, false, true);
                player.sendActionBar(Component.text("Extracted").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            } else {
                extractionMap.put(player, newTime);
                player.sendActionBar(Component.text("Extracting in " + (EXTRACTION_TIME - newTime)).color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            }
        }
    }

    public static long getPollingRate() {
        return POLLING_RATE;
    }

    public static class PollingTask extends BukkitRunnable {

        private final GameManager manager;

        public PollingTask(GameManager manager) {
            this.manager = manager;
        }

        @Override
        public void run() {
            final Map<Player, Vector> map = new HashMap<>();
            for (final Player player : manager.getPlayers()) {
                map.put(player, player.getLocation().toVector());
            }
            for (final Extraction extraction : manager.getExtractions()) {
                extraction.process(map);
            }
        }
    }
}