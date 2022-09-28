package dev.dpvb.survive.util.messages;

import dev.dpvb.survive.game.GameManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
     * Send this message to all players in the game.
     */
    default void sendGame() {
        send(GameManager.getInstance());
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
     * @return a new Message with the MiniMessage string as its source
     */
    static Message mini(@NotNull String message) {
        return from(MiniMessageService.build(message));
    }

    /**
     * Create a message from a MiniMessage string using custom tag resolution.
     *
     * @param message a MiniMessage string
     * @param resolver an additional tag resolver to use
     * @return a new Message from the MiniMessage and <code>resolver</code>
     */
    static Message mini(@NotNull String message, @NotNull TagResolver resolver) {
        return from(MiniMessageService.build(message, resolver));
    }

    /**
     * Create a message from a MiniMessage string using custom tag resolution.
     *
     * @param message a MiniMessage string
     * @param resolvers additional tag resolvers to use
     * @return a new Message from the MiniMessage and <code>resolvers</code>
     */
    static Message mini(@NotNull String message, @NotNull TagResolver... resolvers) {
        return from(MiniMessageService.build(message, resolvers));
    }
}
