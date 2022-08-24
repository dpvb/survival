package dev.dpvb.survive.util.messages;

import dev.dpvb.survive.Survive;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a rule.
 *
 * @param naturalIndex the natural index of the rule
 * @param text the rule text
 */
public record Rule(int naturalIndex, String text) implements Message {
    @Override
    public @NotNull Component asComponent() {
        return MiniMessageService.build(
                Survive.Configuration.getRuleStyle(),
                Placeholder.unparsed("n", String.valueOf(naturalIndex())),
                Placeholder.parsed("rule", text())
        );
    }
}
