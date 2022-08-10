package dev.dpvb.survival.util.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.function.Supplier;

public interface Message extends Supplier<Component> {
    default void send(Audience audience) {
        audience.sendMessage(get());
    }

    default Message replace(String text, String replacement) {
        return () -> get().replaceText(b -> b.matchLiteral(text).replacement(replacement));
    }
}
