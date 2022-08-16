package dev.dpvb.survival.util.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Message extends ComponentLike {
    /**
     * Send this message to an audience.
     *
     * @param audience the audience to send the message to
     */
    default void send(Audience audience) {
        audience.sendMessage(asComponent());
    }

    /**
     * Send this message to the console.
     */
    default void sendConsole() {
        send(Bukkit.getConsoleSender());
    }

    /**
     * Replace text in the message.
     *
     * @param text the text to replace
     * @param replacement the replacement text
     * @return a new message with replaced text
     */
    default Message replace(String text, String replacement) {
        return () -> asComponent().replaceText(b -> b.matchLiteral(text).replacement(replacement));
    }

    /**
     * Create a message from an existing component.
     *
     * @param component a component
     * @return a new message with the component as its source
     */
    static Message from(@NotNull Component component) {
        return () -> component;
    }

    /**
     * Create a message from a MiniMessage string.
     *
     * @param message a MiniMessage string
     * @return a new message with the built MiniMessage string as its source
     */
    static Message mini(@NotNull String message) {
        return from(Messages.build(message));
    }
}
