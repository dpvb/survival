package dev.dpvb.survival.commands;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.destroystokyo.paper.ParticleBuilder;
import dev.dpvb.survival.Survival;
import dev.dpvb.survival.chests.ChestManager;
import dev.dpvb.survival.mongo.models.PlayerInfo;
import dev.dpvb.survival.npc.NPCManager;
import dev.dpvb.survival.npc.storage.StorageNPC;
import dev.dpvb.survival.npc.upgrader.UpgradeNPC;
import dev.dpvb.survival.stats.PlayerInfoManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

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

        manager.command(
                manager.commandBuilder("survivaladmin")
                        .literal("savechests")
                        .argument(IntegerArgument.of("radius"))
                        .senderType(Player.class)
                        .handler(this::saveChestsCommand)
        );

        manager.command(
                manager.commandBuilder("survival")
                        .literal("spawnairdrop")
                        .senderType(Player.class)
                        .handler(this::spawnAirdropCommand)
        );
    }

    private void spawnAirdropCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();

        Bukkit.getScheduler().runTaskTimer(Survival.getInstance(), new Runnable() {

            int radius = 1;
            Location middlePoint = player.getLocation().add(0, 2, 0);
            Set<Location> positions = new HashSet<>();
            int numParticles = 20;
            int counter = 0;

            @Override
            public void run() {

                for (int i = 0; i < numParticles; i++) {
                    double xOff = (radius * counter) * Math.cos(2 * Math.PI * i / numParticles);
                    double zOff = (radius * counter) * Math.sin(2 * Math.PI * i / numParticles);
                    positions.add(middlePoint.clone().add(xOff, 0, zOff));
                }

                for (Location pos : positions) {
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 127, 255), 1.0F);
                    player.spawnParticle(Particle.REDSTONE, pos, 50, dustOptions);
                }

                counter++;
                positions.clear();
            }

        }, 0L, 2L);
    }

    private void saveChestsCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        int radius = ctx.get("radius");
        ChestManager.getInstance().saveChestsToMongo(player.getLocation(), radius);
        player.sendMessage("Saved chests.");
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
