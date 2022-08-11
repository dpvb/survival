package dev.dpvb.survival.util.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface Message extends Supplier<Component> {
    /**
     * Send this message to an audience.
     *
     * @param audience the audience to send the message to
     */
    default void send(Audience audience) {
        audience.sendMessage(get());
    }

    /**
     * Replace text in the message.
     *
     * @param text the text to replace
     * @param replacement the replacement text
     * @return a new message with replaced text
     */
    default Message replace(String text, String replacement) {
        return () -> get().replaceText(b -> b.matchLiteral(text).replacement(replacement));
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
