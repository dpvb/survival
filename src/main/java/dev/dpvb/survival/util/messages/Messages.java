package dev.dpvb.survival.util.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

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
    STANDARD_JOIN_LOG_("{player} joined the arena."),
    STANDARD_LEAVE_LOG_("{player} left the arena."),
    ADMIN_JOIN_LOG_("<gray>{player} joined the game as an admin."),
    ADMIN_LEAVE_LOG_("<gray>{player} left the game as an admin."),
    PLAYER_COUNT_LOG_("<gray>Player count: <green>{count}"),
    LOADED_EXTRACTIONS_LOG_("<gray>Loaded <green>{count} <gray>extraction points in the arena."),
    LOADED_LOOTCHESTS_LOG_("<gray>Loaded <green>{count} <gray>lootchests in the arena."),
    LOADED_SPAWNS_LOG_("<gray>Loaded <green>{count} <gray>spawnpoints in the arena."),
    CLEARED_ITEM_DROPS_LOG_("<gray>Cleared <green>{count} <gray>item drops from the ground."),
    ADMIN_JOIN_SELF("<gray>You have been added to the game."),
    ADMIN_LEAVE_SELF("<gray>You have been removed from the game."),
    TOKEN_AMOUNT_SELF("<gray>You have <green>{tokens} <gray>tokens."),
    SET_TOKEN_AMOUNT("<gray>Set <green>{player}'s <gray>token count to <green>{tokens}<gray>.")
    ;

    private static MiniMessage mm;
    final String message;

    Messages(String message) {
        this.message = message;
    }

    @Override
    public Component get() {
        return build(message);
    }

    public void send(Audience audience) {
        audience.sendMessage(get());
    }

    public static void setBuilder(MiniMessage mm) {
        Messages.mm = mm;
    }

    static Component build(String message) {
        return mm.deserialize(message);
    }

}
