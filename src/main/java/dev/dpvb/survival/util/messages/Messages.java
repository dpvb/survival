package dev.dpvb.survival.util.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public enum Messages implements Message {

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
    @Counting("extraction_points")
    LOADED_EXTRACTIONS_LOG_("<gray>Loaded <green>{count}</green> extraction point(s) in the arena."),
    @Counting("lootchests")
    LOADED_LOOTCHESTS_LOG_("<gray>Loaded <green>{count}</green> lootchest(s) in the arena."),
    @Counting("spawnpoints")
    LOADED_SPAWNS_LOG_("<gray>Loaded <green>{count}</green> spawnpoint(s) in the arena."),
    @Counting("item_drops")
    CLEARED_ITEM_DROPS_LOG_("<gray>Cleared <green>{count}</green> item drop(s) from the ground."),
    ADMIN_JOIN_SELF("<gray>You have been added to the game."),
    ADMIN_LEAVE_SELF("<gray>You have been removed from the game."),
    @Counting("tokens")
    TOKEN_AMOUNT_SELF("<gray>You have <green>{tokens}</green> token(s)."),
    SET_TOKEN_AMOUNT("<gray>Set <green>{player}'s</green> token count to <green>{tokens}</green>.")
    ;

    private static MiniMessage mm;
    final String message;

    Messages(String message) {
        this.message = message;
    }

    @Override
    public @NotNull Component asComponent() {
        return build(message);
    }

    public static void setBuilder(MiniMessage mm) {
        Messages.mm = mm;
    }

    static Component build(@NotNull String message) {
        return mm.deserialize(message);
    }
}
