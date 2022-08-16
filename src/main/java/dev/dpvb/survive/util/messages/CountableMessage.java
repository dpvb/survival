package dev.dpvb.survive.util.messages;

import org.jetbrains.annotations.NotNull;

public interface CountableMessage extends Message {
    /**
     * Get the counted variant of this message.
     *
     * @param amounts amounts to use
     * @return a message with the counts replaced
     * @implNote Default implementation returns {@code this}.
     * @see Counting
     */
    default Message counted(@NotNull Number... amounts) {
        return this;
    }
}
