package dev.dpvb.survival.commands;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import dev.dpvb.survival.chests.ChestManager;
import dev.dpvb.survival.chests.airdrop.AirdropManager;
import dev.dpvb.survival.game.GameManager;
import dev.dpvb.survival.npc.NPCManager;
import dev.dpvb.survival.npc.enchanting.AdvancedEnchanterNPC;
import dev.dpvb.survival.npc.enchanting.BasicEnchanterNPC;
import dev.dpvb.survival.npc.storage.StorageNPC;
import dev.dpvb.survival.npc.upgrader.UpgradeNPC;
import dev.dpvb.survival.stats.PlayerInfoManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class Commands {

    private final BukkitCommandManager<CommandSender> manager;

    public Commands(BukkitCommandManager<CommandSender> manager) {
        this.manager = manager;
    }

    public void initCommands() {
        // ------- GAME COMMANDS -------
        manager.command(
                manager.commandBuilder("survival")
                        .literal("join")
                        .senderType(Player.class)
                        .handler(this::gameJoinCommand)
        );

        manager.command(
                manager.commandBuilder("survival")
                        .literal("leave")
                        .senderType(Player.class)
                        .handler(this::gameLeaveCommand)
        );

        // ------- NPC COMMANDS -------
        manager.command(
                manager.commandBuilder("survivaladmin", "sa")
                        .literal("npc")
                        .literal("create")
                        .argument(StringArgument.<CommandSender>newBuilder("type")
                                .withSuggestionsProvider((ctx, str) -> Arrays.asList(
                                        "basic-enchanter",
                                        "advanced-enchanter",
                                        "upgrader",
                                        "storage")))
                        .senderType(Player.class)
                        .handler(this::npcCreateCommand)
        );

        // ------- TOKEN COMMANDS -------
        manager.command(
                manager.commandBuilder("survival")
                        .literal("tokens")
                        .senderType(Player.class)
                        .handler(this::tokenCommand)
        );

        manager.command(
                manager.commandBuilder("survivaladmin", "sa")
                        .literal("token")
                        .literal("set")
                        .argument(PlayerArgument.of("player"))
                        .argument(IntegerArgument.of("tokens"))
                        .senderType(Player.class)
                        .handler(this::tokenSetCommand)
        );

        // ------- OTHER COMMANDS -------
        manager.command(
                manager.commandBuilder("survival")
                        .literal("test")
                        .senderType(Player.class)
                        .permission("survival.test")
                        .handler(this::testCommand)
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

    private void gameLeaveCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        GameManager manager = GameManager.getInstance();
        if (manager.playerInGame(player)) {
            // TODO: ask player if they fr, mention extraction
            manager.dropAndClearInventory(player);
            manager.sendToHub(player);
            manager.remove(player);
        } else {
            player.sendMessage("You are not in the game.");
        }
    }

    private void gameJoinCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        GameManager manager = GameManager.getInstance();
        if (!manager.playerInGame(player)) {
            manager.join(player);
        } else {
            player.sendMessage("You are already in the game.");
        }
    }

    private void npcCreateCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        String type = ctx.get("type");
        switch (type) {
            case "basic-enchanter" -> NPCManager.getInstance().addNPC(new BasicEnchanterNPC(player.getLocation()));
            case "advanced-enchanter" -> NPCManager.getInstance().addNPC(new AdvancedEnchanterNPC(player.getLocation()));
            case "upgrader" -> NPCManager.getInstance().addNPC(new UpgradeNPC(player.getLocation()));
            case "storage" -> NPCManager.getInstance().addNPC(new StorageNPC(player.getLocation()));
            default -> {
                player.sendMessage("That is not a valid NPC type.");
                return;
            }
        }

        player.sendMessage("You created an NPC.");
    }

    private void spawnAirdropCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        AirdropManager.getInstance().startAirdrop(player.getLocation());
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
