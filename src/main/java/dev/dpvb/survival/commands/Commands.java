package dev.dpvb.survival.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import dev.dpvb.survival.Survival;
import dev.dpvb.survival.chests.tiered.ChestManager;
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
import dev.dpvb.survival.util.messages.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Commands {

    private final PaperCommandManager<CommandSender> manager;
    private final CommandConfirmationManager<CommandSender> leaveConfirmationManager;
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
        this.leaveConfirmationManager = new CommandConfirmationManager<>(
                30L,
                TimeUnit.SECONDS,
                this::confirmLeave,
                player -> player.sendMessage(Component.text("Please run the command again.").color(NamedTextColor.DARK_RED))
        );
    }

    public void initCommands() {
        new AnnotationParser<>(manager, CommandSender.class, parameters -> SimpleCommandMeta.empty()).parse(this);
        leaveConfirmationManager.registerConfirmationProcessor(manager);
        manager.command(
                manager.commandBuilder("confirm")
                        .literal("leave")
                        .handler(leaveConfirmationManager.createConfirmationExecutionHandler())
                        .hidden()
                        .build()
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
        manager.join(player);
    }

    @CommandMethod(value = "survival leave", requiredSender = Player.class)
    @CommandPermission("survival.leave")
    @Confirmation
    void gameLeaveCommand(Player player) {
        GameManager manager = GameManager.getInstance();
        // no-op if the player is not in the game
        if (manager.playerInGame(player)) {
            manager.dropAndClearInventory(player);
            manager.sendToHub(player);
            manager.remove(player);
        }
    }

    private void confirmLeave(CommandPostprocessingContext<CommandSender> context) {
        final var player = (Player) context.getCommandContext().getSender();
        if (GameManager.getInstance().playerInGame(player)) {
            // Ask player if they really, truly want to leave, mentioning the advantages of extracting
            player.sendMessage(
                    Component.text("The best way to leave the game is to ").applyFallbackStyle(Style.style(NamedTextColor.GRAY))
                            .append(Component.text("extract").decorate(TextDecoration.BOLD))
                            .append(Component.text("; that way, you keep your items.").decorate(TextDecoration.ITALIC))
            );
            player.sendMessage(
                    Component.text("Are you sure you want to leave?")
                            .color(NamedTextColor.YELLOW)
                            .append(Component.space())
                            .append(Component.text("[Yes]")
                                    .color(NamedTextColor.RED)
                                    .decorate(TextDecoration.BOLD)
                                    .hoverEvent(HoverEvent.showText(Component.text("Click to leave.")))
                                    .clickEvent(ClickEvent.runCommand("/confirm leave"))
                            )
            );
            player.sendMessage(Component.text("Your items will drop and remain here... hopefully.").decorate(TextDecoration.ITALIC).color(NamedTextColor.RED));
        } else {
            Messages.NOT_IN_GAME.send(player);
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
            Messages.GAME_NOT_RUNNING.send(sender);
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

    @CommandMethod(value = "survivaladmin|sa cleardrops")
    @CommandPermission("survival.admin.cleardrops")
    void clearItemDrops(CommandSender sender) {
        GameManager.getInstance().clearDropsOnGround();
    }

    @CommandMethod(value = "survivaladmin|sa join", requiredSender = Player.class)
    @CommandPermission("survival.admin.join")
    @CommandDescription("Tool to 'join' the game without respawn (unless necessary).")
    void adminJoin(Player player) {
        GameManager manager = GameManager.getInstance();
        if (!manager.isRunning()) {
            Messages.GAME_NOT_RUNNING.send(player);
            return;
        }
        manager.adminJoin(player);
    }

    @CommandMethod(value = "survivaladmin|sa leave", requiredSender = Player.class)
    @CommandPermission("survival.admin.leave")
    @CommandDescription("Tool to 'leave' the game without teleporting to the hub or dropping inventory.")
    void adminLeave(Player player) {
        GameManager manager = GameManager.getInstance();
        if (manager.playerInGame(player)) {
            manager.remove(player);
            Messages.ADMIN_LEAVE_SELF.send(player);
        } else {
            Messages.NOT_IN_GAME.send(player);
        }
    }

}
