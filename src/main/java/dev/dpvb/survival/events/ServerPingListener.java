package dev.dpvb.survival.events;

import dev.dpvb.survival.util.messages.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.motd(Message.mini("<green><bold>SURVIVAL <red><bold>[1.19]").asComponent());
    }

}
