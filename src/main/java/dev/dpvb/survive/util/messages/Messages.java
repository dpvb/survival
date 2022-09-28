package dev.dpvb.survive.util.messages;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Messages {
    private static final Messages INSTANCE = new Messages();
    private ResourceBundle gameMessages;
    private Messages() {}

    public static void init() throws MissingResourceException {
        INSTANCE.gameMessages = PropertyResourceBundle.getBundle("game/game_messages");
    }

    /**
     * Get game-related messages.
     * <p>
     * Game messages are stored in {@code game/game_messages.properties}.
     *
     * @param key the key of the game message
     * @return a new Message object
     */
    public static Message game(@PropertyKey(resourceBundle = "game.game_messages") String key) {
        return Message.mini(INSTANCE.gameMessages.getString(key));
    }

    /**
     * Messages which rely on some kind of count.
     */
    public enum Count implements CountableMessage {
        @Counting(value = "extraction_points",
                zero = "<yellow><bold>No</bold> extraction points loaded</yellow>",
                one = "Loaded <green>{count}</green> extraction point",
                many = "Loaded <green>{count}</green> extraction points")
        LOADED_EXTRACTIONS_LOG_("<gray>{extraction_points} in the arena."),
        @Counting(value = "lootchests",
                zero = "<yellow><bold>No</bold> lootchests loaded</yellow>",
                one = "Loaded <green>{count}</green> lootchest",
                many = "Loaded <green>{count}</green> lootchests")
        LOADED_LOOTCHESTS_LOG_("<gray>{lootchests} in the arena."),
        @Counting(value = "spawnpoints",
                zero = "<yellow><bold>No</bold> spawnpoints loaded</yellow>",
                one = "Loaded <green>{count}</green> spawnpoint",
                many = "Loaded <green>{count}</green> spawnpoints")
        LOADED_SPAWNS_LOG_("<gray>{spawnpoints} in the arena."),
        @Counting(value = "drops",
                zero = "<yellow>No</yellow> item drops were cleared",
                one = "Cleared <green>{count}</green> item drop",
                many = "Cleared <green>{count}</green> item drops")
        CLEARED_ITEM_DROPS_LOG_("<gray>{drops} from the ground."),
        @Counting(value = "tokens",
                zero = "<yellow>no tokens</yellow>",
                one = "<green>{count}</green> token",
                many = "<green>{count}</green> tokens")
        TOKEN_AMOUNT_SELF_("<gray>You have {tokens}."),
        ;

        final String message;
        final Counting.Counted[] counters;

        Count(String message) {
            this.message = message;
            final Counting[] arr;
            try {
                arr = Count.class.getDeclaredField(name()).getDeclaredAnnotationsByType(Counting.class);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException(e);
            }
            if (arr.length > 0) {
                this.counters = new Counting.Counted[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    counters[i] = new Counting.Counted(arr[i]);
                }
            } else {
                this.counters = null;
            }
        }

        @Override
        public @NotNull Component asComponent() {
            return MiniMessageService.build(message);
        }

        @Override
        public Message counted(@NotNull Number... amounts) {
            if (amounts.length == 0) return this;
            if (counters == null) return this; // graceful
            final var sb = new StringBuilder(message);
            int c = 0;
            for (Counting.Counted counter : counters) {
                int source = counter.arg();
                if (source == -1) {
                    // use c
                    if (c < amounts.length) {
                        source = c;
                    } else {
                        // amounts is too short, try next counter
                        continue;
                    }
                }
                final var placeholder = "{" + counter.placeholder() + "}";
                int index = sb.indexOf(placeholder);
                while (index != -1) {
                    sb.delete(index, index + placeholder.length());
                    final var process = counter.process(amounts[source]);
                    if (process.contains(placeholder)) {
                        throw new IllegalStateException("Replacements cannot contain their placeholder!");
                    }
                    sb.insert(index, process);
                    index = sb.indexOf(placeholder);
                }
                ++c;
            }
            return Message.mini(sb.toString());
        }
    }
}
