package dev.dpvb.survival;

import cloud.commandframework.CommandTree;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import dev.dpvb.survival.chests.ChestManager;
import dev.dpvb.survival.chests.airdrop.AirdropManager;
import dev.dpvb.survival.commands.Commands;
import dev.dpvb.survival.events.FirstJoinListener;
import dev.dpvb.survival.game.GameManager;
import dev.dpvb.survival.mongo.MongoManager;
import dev.dpvb.survival.npc.listeners.NPCListener;
import dev.dpvb.survival.npc.NPCManager;
import dev.dpvb.survival.npc.storage.StorageManager;
import dev.dpvb.survival.stats.PlayerInfoManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public final class Survival extends JavaPlugin {

    private static Survival instance;
    private BukkitCommandManager<CommandSender> manager;

    @Override
    public void onEnable() {
        // Init instance of Main class
        instance = this;
        // Setup Config File
        setupConfigFile();
        // Setup Managers
        setupManagers();
        // Setup Commands
        setupCommands();
        // Register Listeners
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Upload PlayerInfo to Mongo
        PlayerInfoManager.getInstance().save();
        // Upload Storage to Mongo
        StorageManager.getInstance().save();
        // Clear all Airdrop Chests
        AirdropManager.getInstance().clearAirdrops();
    }

    private void setupManagers() {
        // Setup Mongo
        MongoManager.getInstance();
        // Load Player Info Statistics from Mongo
        PlayerInfoManager.getInstance().load();
        // Load all Player Storage from Mongo
        StorageManager.getInstance().load();
        // Load all Chest Data
        ChestManager.getInstance().loadLootChests();
        // Load Airdrop Information
        AirdropManager.getInstance().loadAirdropAnimation();
        // Setup Game Manager
        GameManager.getInstance();
        // Load NPCs
        Bukkit.getScheduler().runTaskLater(this, NPCManager.getInstance()::loadNPCs, 5);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        Bukkit.getPluginManager().registerEvents(new FirstJoinListener(), this);
    }

    private void setupCommands() {
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                CommandExecutionCoordinator.simpleCoordinator();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.manager = new PaperCommandManager<>(
                    this,
                    executionCoordinatorFunction,
                    mapperFunction,
                    mapperFunction
            );
        } catch (final Exception e) {
            this.getLogger().severe("Failed to initialize the command manager.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new Commands(manager).initCommands();
    }

    private void setupConfigFile() {
        saveDefaultConfig();
        Configuration.config = getConfig();
    }

    public static Survival getInstance() {
        return instance;
    }

    public static class Configuration {
        private static FileConfiguration config;

        public static ConfigurationSection getNPCSkinSection() {
            return config.getConfigurationSection("npc-skins");
        }

        public static ConfigurationSection getEnchantingSection() {
            return config.getConfigurationSection("enchanting");
        }

        public static ConfigurationSection getUpgradingSection() {
            return config.getConfigurationSection("upgrades");
        }

        public static ConfigurationSection getLootSection() {
            return config.getConfigurationSection("loot");
        }

        public static String getMongoConnectionString() {
            return config.getString("mongo.connection-string");
        }

        public static String getMongoUsername() {
            return config.getString("mongo.username");
        }

        public static String getMongoPassword() {
            return config.getString("mongo.password");
        }
    }
}
