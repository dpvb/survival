package dev.dpvb.survive.events;

import dev.dpvb.survive.Survive;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.stream.Collectors;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDeathLogOriginal(PlayerDeathEvent event) {
        final var logger = Survive.getInstance().getLogger();
        final var component = event.deathMessage();
        if (component == null) {
            logger.info("PlayerDeathEvent.deathMessage() returned null");
            return;
        }
        logger.info("Debug: Logging death message...");
        // Print component to console
        logger.info(component.toString());
        // Check if the death message is a TranslatableComponent
        if (component instanceof TranslatableComponent tc) {
            logger.info("Death message is-a TranslatableComponent");
            logger.info("Translation key = " + tc.key());
            logger.info("Args = " + tc.args().stream().map(Object::toString).collect(Collectors.joining("',\n'", "[\n'", "'\n]")));
        }
    }

}
