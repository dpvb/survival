package dev.dpvb.survival.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
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
import dev.dpvb.survival.npc.tokentrader.TokenTraderNPC;
import dev.dpvb.survival.npc.upgrader.UpgradeNPC;
import dev.dpvb.survival.stats.PlayerInfoManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Commands {

    private final PaperCommandManager<CommandSender> manager;
    private final CommandConfirmationManager<CommandSender> confirmationManager;
    final List<String> npcCreateSuggestions = List.of(
            "basic-enchanter",
            "advanced-enchanter",
            "upgrader",
            "storage",
            "join",
            "token-trader"
    );

    public Commands() throws Exception {
        this.manager = PaperCommandManager.createNative(Survival.getInstance(), CommandExecutionCoordinator.simpleCoordinator());
        this.confirmationManager = new CommandConfirmationManager<>(
                30L,
                TimeUnit.SECONDS,
                context -> {
                    final var sender = context.getCommandContext().getSender();
                    sender.sendMessage(
                            Component.text("Are you sure you want to leave?")
                                    .color(NamedTextColor.YELLOW)
                                    .append(Component.space())
                                    .append(Component.text("Yes").color(NamedTextColor.RED)
                                            .clickEvent(ClickEvent.runCommand("/confirm"))
                                    ));
                    sender.sendMessage(Component.text("Your items will drop and remain here...hopefully.").decorate(TextDecoration.ITALIC).color(NamedTextColor.GRAY));
                },
                player -> {
                    // no-op
                }
        );
        confirmationManager.registerConfirmationProcessor(manager);
        manager.command(
                manager.commandBuilder("confirm")
                        .meta(CommandMeta.DESCRIPTION, "Confirm a command")
                        .handler(confirmationManager.createConfirmationExecutionHandler())
                        .build()
        );
    }

    public void initCommands() {
        new AnnotationParser<>(manager, CommandSender.class, parameters -> SimpleCommandMeta.empty()).parse(this);
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
    @Confirmation
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

    // ------- NPC COMMANDS -------
    @Suggestions("npc-type")
    public List<String> npcCreateSuggestions(CommandContext<Player> sender, String input) {
        List<String> list = new ArrayList<>(npcCreateSuggestions.size());
        final var lowerCase = input.toLowerCase();
        for (String suggestion : npcCreateSuggestions) {
            if (suggestion.startsWith(lowerCase)) {
                list.add(suggestion);
            }
        }
        return list;
    }

    @CommandMethod(value = "survivaladmin|sa npc create <type>", requiredSender = Player.class)
    @CommandPermission("survival.admin.npc.create")
    void npcCreateCommand(Player player, @Argument(value = "type", suggestions = "npc-type") String type) {
        switch (type) {
            case "basic-enchanter" -> NPCManager.getInstance().addNPC(new BasicEnchanterNPC(player.getLocation()));
            case "advanced-enchanter" -> NPCManager.getInstance().addNPC(new AdvancedEnchanterNPC(player.getLocation()));
            case "upgrader" -> NPCManager.getInstance().addNPC(new UpgradeNPC(player.getLocation()));
            case "storage" -> NPCManager.getInstance().addNPC(new StorageNPC(player.getLocation()));
            case "join" -> NPCManager.getInstance().addNPC(new JoinNPC(player.getLocation()));
            case "token-trader" -> NPCManager.getInstance().addNPC(new TokenTraderNPC(player.getLocation()));
            default -> {
                player.sendMessage("That is not a valid NPC type.");
                return;
            }
        }
        player.sendMessage("You created an NPC.");
    }

    // ------- TOKEN COMMANDS -------
    @CommandMethod(value = "survival tokens", requiredSender = Player.class)
    @CommandPermission("survival.tokens")
    void tokenCommand(Player player) {
        int tokens = PlayerInfoManager.getInstance().getTokens(player.getUniqueId());

        player.sendMessage(Component.text("You have " + tokens + " tokens.").color(NamedTextColor.YELLOW));
    }

    @CommandMethod(value = "survivaladmin|sa token set <player> <tokens>")
    @CommandPermission("survival.admin.token.set")
    void tokenSetCommand(CommandSender sender, @Argument("player") Player player, @Argument("tokens") int tokens) {
        PlayerInfoManager.getInstance().setTokens(player.getUniqueId(), tokens);
        sender.sendMessage(Component.text("Set " + player.getName() + "'s token count to " + tokens).color(NamedTextColor.YELLOW));
    }

    // ------- SETUP COMMANDS -------
    @CommandMethod(value = "survivaladmin|sa setextract", requiredSender = Player.class)
    @CommandPermission("survival.admin.extract.set")
    void setExtractionRegionCommand(Player player) {
        new ExtractionRegionSelector(player, region -> {
            MongoManager.getInstance().getExtractionRegionService().create(region);
            Bukkit.getLogger().info("Uploaded an Extraction Region to Mongo.");
        });
    }

    @CommandMethod(value = "survivaladmin|sa setspawns", requiredSender = Player.class)
    @CommandPermission("survival.admin.spawn.set")
    void setArenaSpawnsCommand(Player player) {
        new SpawnTool(player);
    }

    // ------- STATE COMMANDS -------
    @CommandMethod(value = "survivaladmin|sa start")
    @CommandPermission("survival.admin.start")
    void startGameCommand(CommandSender sender) {
        if (GameManager.getInstance().isRunning()) {
            sender.sendMessage(Component.text("The game is still running.").color(NamedTextColor.DARK_RED));
            return;
        }
        GameManager.getInstance().start();
        sender.sendMessage(Component.text("Game started.").color(NamedTextColor.GREEN));
    }

    @CommandMethod(value = "survivaladmin|sa stop")
    @CommandPermission("survival.admin.stop")
    void stopGameCommand(CommandSender sender) {
        if (!GameManager.getInstance().isRunning()) {
            sender.sendMessage(Component.text("The game is not running.").color(NamedTextColor.YELLOW));
            return;
        }
        GameManager.getInstance().stop();
        sender.sendMessage(Component.text("Game stopped.").color(NamedTextColor.DARK_GREEN));
    }

    // ------- OTHER COMMANDS -------
    @CommandMethod(value = "survival test", requiredSender = Player.class)
    @CommandPermission("survival.test")
    void testCommand(Player player) {
        player.getInventory().addItem(AirdropManager.getInstance().getAirdropItem());
    }

    @CommandMethod(value = "survivaladmin|sa savechests <radius>", requiredSender = Player.class)
    @CommandPermission("survival.admin.savechests")
    void saveChestsCommand(Player player, @Argument("radius") int radius) {
        ChestManager.getInstance().saveChestsToMongo(player.getLocation(), radius);
        player.sendMessage("Saved chests.");
    }

}
