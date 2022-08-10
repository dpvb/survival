package dev.dpvb.survival.util.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public enum Messages {

    NOT_IN_GAME("<red>You are not in game."),
    AIRDROP_INCORRECT_PLACEMENT("<red>Airdrop needs empty space above to drop!");

    private static MiniMessage mm;
    final String message;

    Messages(String message) {
        this.message = message;
    }

    public Component get() {
        return mm.deserialize(message);
    }

    public void send(Audience audience) {
        audience.sendMessage(get());
    }

    public static void setBuilder(MiniMessage mm) {
        Messages.mm = mm;
    }

}
