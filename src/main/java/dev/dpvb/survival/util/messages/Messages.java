package dev.dpvb.survival.util.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public enum Messages implements Message {

    NOT_IN_GAME("<red>You are not in the game."),
    ALREADY_IN_GAME("<red>You are already in the game."),
    GAME_NOT_RUNNING("<yellow>The game is not running."),
    AIRDROP_INCORRECT_PLACEMENT("<red>Airdrop needs empty space above to drop!"),
    STANDARD_JOIN_LOG_("{player} joined the arena."),
    STANDARD_LEAVE_LOG_("{player} left the arena."),
    ADMIN_JOIN_LOG_("<gray>{player} joined the game as an admin."),
    ADMIN_LEAVE_LOG_("<gray>{player} left the game as an admin."),
    ADMIN_JOIN_SELF("<gray>You have been added to the game."),
    ADMIN_LEAVE_SELF("<gray>You have been removed from the game."),
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

    public static Component build(String message) {
        return mm.deserialize(message);
    }

}
