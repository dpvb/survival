package dev.dpvb.survival;

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
import dev.dpvb.survival.util.messages.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Survival extends JavaPlugin {

    private static Survival instance;

    @Override
    public void onEnable() {
        // Init instance of Main class
        instance = this;
        // Setup Config File
        setupConfigFile();
        // Setup Messages
        setupMessages();
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
        // Remove all Players from the Arena
        GameManager.getInstance().removeAllPlayers(false);
    }

    private void setupConfigFile() {
        saveDefaultConfig();
        Configuration.config = getConfig();
    }

    private void setupMessages() {
        Messages.setBuilder(MiniMessage.builder()
                .tags(TagResolver.standard())
                .build());
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
        // Setup Game Manager and start game
        GameManager.getInstance().start();
        // Load NPCs
        Bukkit.getScheduler().runTaskLater(this, NPCManager.getInstance()::loadNPCs, 5);
    }

    private void setupCommands() {
        final Commands commands;
        try {
            commands = new Commands();
        } catch (final Exception e) {
            this.getLogger().severe("Failed to initialize the command manager.");
            throw new IllegalStateException(e);
        }
        commands.initCommands();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        Bukkit.getPluginManager().registerEvents(new FirstJoinListener(), this);
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

        public static ConfigurationSection getTokenTraderSection() {
            return config.getConfigurationSection("token-trader");
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
