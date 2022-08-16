package dev.dpvb.survive.events;

import dev.dpvb.survive.util.messages.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.motd(Message.mini("<green><bold>SURVIVE <red><bold>[1.19]").asComponent());
    }

}
