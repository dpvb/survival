package dev.dpvb.survival.util.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

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
}
