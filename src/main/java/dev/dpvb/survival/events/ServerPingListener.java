package dev.dpvb.survival.events;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.motd(MiniMessage.miniMessage().deserialize("<green><bold>SURVIVAL <red><bold>[1.19]", TagResolver.standard()));
    }

}
