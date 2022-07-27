package dev.dpvb.survival.commands;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import dev.dpvb.survival.mongo.models.PlayerInfo;
import dev.dpvb.survival.npc.NPCManager;
import dev.dpvb.survival.npc.storage.StorageNPC;
import dev.dpvb.survival.npc.upgrader.UpgradeNPC;
import dev.dpvb.survival.stats.PlayerInfoManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

        manager.command(
                manager.commandBuilder("survival")
                        .literal("tokens")
                        .senderType(Player.class)
                        .handler(this::tokenCommand)
        );

        manager.command(
                manager.commandBuilder("survivaladmin")
                        .literal("token")
                        .literal("set")
                        .argument(PlayerArgument.of("player"))
                        .argument(IntegerArgument.of("tokens"))
                        .senderType(Player.class)
                        .handler(this::tokenSetCommand)
        );
    }

    private void tokenCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        int tokens = PlayerInfoManager.getInstance().getTokens(player.getUniqueId());

        player.sendMessage(Component.text("You have " + tokens + " tokens.").color(NamedTextColor.YELLOW));
    }

    private void tokenSetCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = ctx.get("player");
        int tokens = ctx.get("tokens");

        if (player == null) {
            return;
        }

        PlayerInfoManager.getInstance().setTokens(player.getUniqueId(), tokens);
        ctx.getSender().sendMessage(Component.text("Set " + player.getName() + "'s token count to " + tokens).color(NamedTextColor.YELLOW));
    }

    private void testCommand(final @NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        NPCManager.getInstance().addNPC(new UpgradeNPC(player.getLocation()));
    }


}
