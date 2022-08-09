package dev.dpvb.survival.util.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public enum Messages {

    NOT_INGAME("<red>You are not in game."),
    AIRDROP_INCORRECT_PLACEMENT("<red>Airdrop needs empty space above to drop!");

    private String message;
    private static MiniMessage mm;

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
