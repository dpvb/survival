package dev.dpvb.survive;

import dev.dpvb.survive.chests.airdrop.AirdropManager;
import dev.dpvb.survive.commands.Commands;
import dev.dpvb.survive.events.FirstJoinListener;
import dev.dpvb.survive.events.PlayerDeathListener;
import dev.dpvb.survive.events.PlayerJoinQuitMessageListener;
import dev.dpvb.survive.events.ServerPingListener;
import dev.dpvb.survive.events.SpawnPlayerOnJoinListener;
import dev.dpvb.survive.game.GameManager;
import dev.dpvb.survive.game.RuleManager;
import dev.dpvb.survive.integrations.placeholderapi.PlaceholderAPIHook;
import dev.dpvb.survive.mongo.MongoManager;
import dev.dpvb.survive.npc.listeners.NPCListener;
import dev.dpvb.survive.npc.NPCManager;
import dev.dpvb.survive.npc.storage.StorageManager;
import dev.dpvb.survive.stats.PlayerInfoManager;
import dev.dpvb.survive.util.messages.MiniMessageService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Survive extends JavaPlugin {

    private static Survive instance;

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
        // Register Placeholders
        registerPlaceholders();
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
        MiniMessageService.setMiniMessage(MiniMessage.builder()
                .tags(TagResolver.standard())
                .build());
    }

    private void setupManagers() {
        // Setup Mongo
        MongoManager.getInstance();
        // Setup Rules
        RuleManager.getInstance().load();
        // Load Player Info Statistics from Mongo
        PlayerInfoManager.getInstance().load();
        // Load all Player Storage from Mongo
        StorageManager.getInstance().load();
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
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitMessageListener(), this);
        Bukkit.getPluginManager().registerEvents(new ServerPingListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnPlayerOnJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    private void registerPlaceholders() {
        try {
            new PlaceholderAPIHook();
        } catch (IllegalStateException e) {
            this.getLogger().info("PlaceholderAPI not found. Skipping placeholder registration.");
        }
    }

    public static Survive getInstance() {
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

        public static String getRuleStyle() {
            return config.getString("rule-style");
        }

        public static List<String> getRulesList() {
            return config.getStringList("rules");
        }
    }
}
