package dev.dpvb.survive.events;

import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static dev.dpvb.survive.util.messages.DeathMessage.from;

public class PlayerDeathListener implements Listener {

    final ResourceBundle deathMessages;

    public PlayerDeathListener() {
        deathMessages = PropertyResourceBundle.getBundle("game/death_messages");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeathEdit(PlayerDeathEvent event) {
        final var component = event.deathMessage();
        if (component == null) return;
        // Check if the death message is a TranslatableComponent
        if (component instanceof TranslatableComponent tc) {
            if (deathMessages.containsKey(tc.key())) {
                event.deathMessage(from(tc, deathMessages.getString(tc.key())).asComponent());
            }
        }
    }

}
