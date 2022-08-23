package dev.dpvb.survive.util.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface DeathMessage extends ComponentLike {
    /**
     * Get the MiniMessage-formatted source string.
     *
     * @return the source string for this message
     */
    @NotNull String miniMessage();

    /**
     * Get the list of death message argument representations.
     *
     * @return a list of Components from the original death message
     */
    @NotNull List<? extends ComponentLike> args();

    @Override
    default @NotNull Component asComponent() {
        final var args = args();
        final String miniMessage = miniMessage();
        final int size = args.size();
        if (size == 0) {
            return MiniMessageService.build(miniMessage);
        } else if (size == 1) {
            return MiniMessageService.build(miniMessage, Placeholder.component("1", args.get(0)));
        }
        final List<TagResolver> resolvers = new ArrayList<>(size);
        final var iterator = args.iterator();
        int naturalIndex = 1;
        while (iterator.hasNext() && naturalIndex <= size) {
            resolvers.add(Placeholder.component(String.valueOf(naturalIndex++), iterator.next()));
        }
        return MiniMessageService.build(miniMessage, resolvers.toArray(new TagResolver[0]));
    }

    class DeathMessageImpl implements DeathMessage {
        private final String miniMessage;
        private final List<? extends ComponentLike> args;

        DeathMessageImpl(String miniMessage, List<? extends ComponentLike> args) {
            this.miniMessage = miniMessage;
            this.args = args;
        }

        @Override
        public @NotNull String miniMessage() {
            return miniMessage;
        }

        @Override
        public @NotNull List<? extends ComponentLike> args() {
            return args;
        }

        @Override
        public String toString() {
            return asComponent().toString();
        }
    }

    /**
     * Create a new death message using the original death message and a
     * MiniMessage-formatted replacement.
     * <p>
     * The arguments embedded in the original death message will be
     * accessible using the following syntax:
     * <pre><code>&lt;n&gt;</code></pre>
     * where <code>n</code> is the natural number of the argument. This
     * complements the lang file's format for numbered arguments--indexes
     * start at 1 in those files as well.
     *
     * @param original the original death message
     * @param miniMessageReplacement a MiniMessage-formatted replacement
     * @return a new death message
     */
    static DeathMessage from(@NotNull TranslatableComponent original, @NotNull String miniMessageReplacement) {
        return new DeathMessageImpl(miniMessageReplacement, original.args());
    }

}
