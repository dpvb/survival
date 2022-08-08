package dev.dpvb.survival.commands;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import dev.dpvb.survival.Survival;
import dev.dpvb.survival.chests.ChestManager;
import dev.dpvb.survival.chests.airdrop.AirdropManager;
import dev.dpvb.survival.game.GameManager;
import dev.dpvb.survival.game.extraction.ExtractionRegionSelector;
import dev.dpvb.survival.game.spawn.SpawnTool;
import dev.dpvb.survival.mongo.MongoManager;
import dev.dpvb.survival.npc.NPCManager;
import dev.dpvb.survival.npc.enchanting.AdvancedEnchanterNPC;
import dev.dpvb.survival.npc.enchanting.BasicEnchanterNPC;
import dev.dpvb.survival.npc.join.JoinNPC;
import dev.dpvb.survival.npc.storage.StorageNPC;
import dev.dpvb.survival.npc.upgrader.UpgradeNPC;
import dev.dpvb.survival.stats.PlayerInfoManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class Commands {

    private final PaperCommandManager<CommandSender> manager;

    public Commands() throws Exception {
        this.manager = PaperCommandManager.createNative(Survival.getInstance(), CommandExecutionCoordinator.simpleCoordinator());
    }

    public void initCommands() {
        new AnnotationParser<>(manager, CommandSender.class, parameters -> SimpleCommandMeta.empty()).parse(this);

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
                                        "storage",
                                        "join")))
                        .senderType(Player.class)
                        .permission("survival.admin.npc.create")
                        .handler(this::npcCreateCommand)
        );

        // ------- TOKEN COMMANDS -------
        manager.command(
                manager.commandBuilder("survival")
                        .literal("tokens")
                        .senderType(Player.class)
                        .permission("survival.tokens")
                        .handler(this::tokenCommand)
        );

        manager.command(
                manager.commandBuilder("survivaladmin", "sa")
                        .literal("token")
                        .literal("set")
                        .argument(PlayerArgument.of("player"))
                        .argument(IntegerArgument.of("tokens"))
                        .permission("survival.admin.token.set")
                        .handler(this::tokenSetCommand)
        );

        // ------- SETUP COMMANDS -------
        manager.command(
                manager.commandBuilder("survivaladmin", "sa")
                        .literal("setextract")
                        .senderType(Player.class)
                        .permission("survival.admin.extract.set")
                        .handler(this::setExtractionRegionCommand)
        );

        manager.command(
                manager.commandBuilder("survivaladmin", "sa")
                        .literal("setspawns")
                        .senderType(Player.class)
                        .permission("survival.admin.spawn.set")
                        .handler(this::setArenaSpawnsCommand)
        );

        // ------- STATE COMMANDS -------
        manager.command(
                manager.commandBuilder("survivaladmin", "sa")
                        .literal("start")
                        .permission("survival.admin.start")
                        .handler(this::startGameCommand)
        );

        manager.command(
                manager.commandBuilder("survivaladmin", "sa")
                        .literal("stop")
                        .permission("survival.admin.stop")
                        .handler(this::stopGameCommand)
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
                        .permission("survival.admin.savechests")
                        .handler(this::saveChestsCommand)
        );

        manager.command(
                manager.commandBuilder("survival")
                        .literal("spawnairdrop")
                        .senderType(Player.class)
                        .permission("survival.spawnairdrop")
                        .handler(this::spawnAirdropCommand)
        );
    }

    // ------- GAME COMMANDS -------
    @CommandMethod(value = "survival join", requiredSender = Player.class)
    @CommandPermission("survival.join")
    void gameJoinCommand(Player player) {
        GameManager manager = GameManager.getInstance();
        if (!manager.isRunning()) {
            player.sendMessage("The game is not running.");
            return;
        }
        if (manager.playerInGame(player) || !manager.join(player)) {
            player.sendMessage("You are already in the game.");
        }
    }

    @CommandMethod(value = "survival leave", requiredSender = Player.class)
    @CommandPermission("survival.leave")
    void gameLeaveCommand(Player player) {
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

    private void npcCreateCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        String type = ctx.get("type");
        switch (type) {
            case "basic-enchanter" -> NPCManager.getInstance().addNPC(new BasicEnchanterNPC(player.getLocation()));
            case "advanced-enchanter" -> NPCManager.getInstance().addNPC(new AdvancedEnchanterNPC(player.getLocation()));
            case "upgrader" -> NPCManager.getInstance().addNPC(new UpgradeNPC(player.getLocation()));
            case "storage" -> NPCManager.getInstance().addNPC(new StorageNPC(player.getLocation()));
            case "join" -> NPCManager.getInstance().addNPC(new JoinNPC(player.getLocation()));
            default -> {
                player.sendMessage("That is not a valid NPC type.");
                return;
            }
        }
        player.sendMessage("You created an NPC.");
    }

    private void tokenCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        int tokens = PlayerInfoManager.getInstance().getTokens(player.getUniqueId());

        player.sendMessage(Component.text("You have " + tokens + " tokens.").color(NamedTextColor.YELLOW));
    }

    private void tokenSetCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = ctx.get("player"); // Added as a required argument, so it is always non-null
        int tokens = ctx.get("tokens");

        PlayerInfoManager.getInstance().setTokens(player.getUniqueId(), tokens);
        ctx.getSender().sendMessage(Component.text("Set " + player.getName() + "'s token count to " + tokens).color(NamedTextColor.YELLOW));
    }

    private void setExtractionRegionCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        new ExtractionRegionSelector(player, region -> {
            MongoManager.getInstance().getExtractionRegionService().create(region);
            Bukkit.getLogger().info("Uploaded an Extraction Region to Mongo.");
        });
    }

    private void setArenaSpawnsCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        new SpawnTool(player);
    }

    private void startGameCommand(@NonNull CommandContext<CommandSender> ctx) {
        if (GameManager.getInstance().isRunning()) {
            ctx.getSender().sendMessage(Component.text("The game is still running.").color(NamedTextColor.DARK_RED));
            return;
        }
        GameManager.getInstance().start();
        ctx.getSender().sendMessage(Component.text("Game started.").color(NamedTextColor.GREEN));
    }

    private void stopGameCommand(@NonNull CommandContext<CommandSender> ctx) {
        if (!GameManager.getInstance().isRunning()) {
            ctx.getSender().sendMessage(Component.text("The game is not running.").color(NamedTextColor.YELLOW));
            return;
        }
        GameManager.getInstance().stop();
        ctx.getSender().sendMessage(Component.text("Game stopped.").color(NamedTextColor.DARK_GREEN));
    }

    private void testCommand(final @NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        NPCManager.getInstance().addNPC(new UpgradeNPC(player.getLocation()));
    }

    private void saveChestsCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        int radius = ctx.get("radius");
        ChestManager.getInstance().saveChestsToMongo(player.getLocation(), radius);
        player.sendMessage("Saved chests.");
    }

    private void spawnAirdropCommand(@NonNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        AirdropManager.getInstance().startAirdrop(player.getLocation());
    }

}
