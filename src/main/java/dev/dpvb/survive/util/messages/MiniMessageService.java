package dev.dpvb.survive.util.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public class MiniMessageService {
    public static final MiniMessageService INSTANCE = new MiniMessageService();

    private MiniMessage miniMessage;

    static Component build(@NotNull String message) {
        return INSTANCE.miniMessage.deserialize(message);
    }

    static Component build(@NotNull String message, TagResolver resolver) {
        return INSTANCE.miniMessage.deserialize(message, resolver);
    }

    static Component build(@NotNull String message, TagResolver... resolvers) {
        return INSTANCE.miniMessage.deserialize(message, resolvers);
    }

    public static void setMiniMessage(MiniMessage miniMessage) {
        INSTANCE.miniMessage = miniMessage;
    }
}
