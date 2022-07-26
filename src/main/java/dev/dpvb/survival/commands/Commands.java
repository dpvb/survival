package dev.dpvb.survival.commands;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.context.CommandContext;
import dev.dpvb.survival.mongo.models.PlayerInfo;
import dev.dpvb.survival.stats.PlayerInfoManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Commands {

    private final BukkitCommandManager<CommandSender> manager;

    public Commands(BukkitCommandManager<CommandSender> manager) {
        this.manager = manager;
    }

    public void initCommands() {
        manager.command(
                manager.commandBuilder("survival")
                        .literal("test")
                        .senderType(Player.class)
                        .permission("survival.test")
                        .handler(this::testCommand)
        );
    }

    private void testCommand(final @NonNull CommandContext<CommandSender> ctx) {

    }

}
