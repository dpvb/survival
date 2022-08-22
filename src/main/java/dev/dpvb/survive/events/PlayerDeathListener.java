package dev.dpvb.survive.events;

import dev.dpvb.survive.Survive;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.stream.Collectors;

import static dev.dpvb.survive.util.messages.DeathMessage.from;

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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeathTryEdit(PlayerDeathEvent event) {
        final var logger = Survive.getInstance().getLogger();
        final var component = event.deathMessage();
        if (component == null) {
            logger.info("PlayerDeathEvent.deathMessage() returned null. No-op (edit)");
            return;
        }
        // Check if the death message is a TranslatableComponent
        if (component instanceof TranslatableComponent tc) {
            logger.info("Death message is-a TranslatableComponent. Editing...");
            logger.info("note: actual mod restricted to just 'death.attack.outOfWorld'");
            final StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= tc.args().size(); ++i) {
                if (i == 1) sb.append("[");
                sb.append("{<").append(i).append(">}");
                if (i == tc.args().size()) sb.append("]");
                else sb.append(", ");
            }
            final var deathMessage = from(tc, "Successful edit! Here are the args: " + sb);
            logger.info("New death message = " + deathMessage);
            if (tc.key().equals("death.attack.outOfWorld")) event.deathMessage(deathMessage.asComponent());
        }
    }

}
