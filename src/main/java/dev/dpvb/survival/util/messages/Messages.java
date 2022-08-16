package dev.dpvb.survival.util.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public enum Messages implements CountableMessage {

    NOT_IN_GAME("<red>You are not in the game."),
    ALREADY_IN_GAME("<red>You are already in the game."),
    GAME_NOT_RUNNING("<yellow>The game is not running."),
    GAME_RUNNING("<red>The game is currently running."),
    GAME_STARTED("<green>The game started."),
    GAME_STOPPED("<green>The game stopped."),
    SAVED_CHESTS("<green>Chests saved."),
    NPC_CREATED("<green>Successfully created NPC."),
    INVALID_NPC_TYPE("<red>That is an invalid NPC type."),
    AIRDROP_INCORRECT_PLACEMENT("<red>Airdrop needs empty space above to drop!"),
    STANDARD_JOIN_LOG_("<green>{player} <gray>joined the arena."),
    STANDARD_LEAVE_LOG_("<green>{player} <gray>left the arena."),
    ADMIN_JOIN_LOG_("<green>{player} <gray>joined the game as an admin."),
    ADMIN_LEAVE_LOG_("<green>{player} <gray>left the game as an admin."),
    PLAYER_COUNT_LOG_("<gray>Player count: <green>{count}"),
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
    ADMIN_JOIN_SELF("<gray>You have been added to the game."),
    ADMIN_LEAVE_SELF("<gray>You have been removed from the game."),
    @Counting(value = "tokens",
            zero = "<yellow>no tokens</yellow>",
            one = "<green>{count}</green> token",
            many = "<green>{count}</green> tokens")
    TOKEN_AMOUNT_SELF_("<gray>You have {tokens}."),
    SET_TOKEN_AMOUNT__("<gray>Set <green>{player}'s</green> token count to <green>{tokens}</green>."),
    CLEARING_DROPS_WARNING("<red>Item drops will be despawned in 30 seconds."),
    DESPAWNED_DROPS("<red>Item drops despawned."),
    ;

    private static final Map<Messages, Counting.Counted[]> COUNTING;
    private static MiniMessage mm;

    static {
        COUNTING = new EnumMap<>(Messages.class);
        for (Messages m : values()) {
            final Counting[] arr;
            try {
                arr = Messages.class.getDeclaredField(m.name()).getDeclaredAnnotationsByType(Counting.class);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException(e);
            }
            if (arr.length > 0) {
                final Counting.Counted[] counters = new Counting.Counted[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    counters[i] = new Counting.Counted(arr[i]);
                }
                COUNTING.put(m, counters);
            }
        }
    }

    final String message;

    Messages(String message) {
        this.message = message;
    }

    @Override
    public @NotNull Component asComponent() {
        return build(message);
    }

    @Override
    public Message counted(@NotNull Number... amounts) {
        if (amounts.length == 0) return this;
        final var counters = COUNTING.get(this);
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

    public static void setBuilder(MiniMessage mm) {
        Messages.mm = mm;
    }

    static Component build(@NotNull String message) {
        return mm.deserialize(message);
    }
}
