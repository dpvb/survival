package dev.dpvb.survival;

import dev.dpvb.survival.commands.TestCommand;
import dev.dpvb.survival.npc.listeners.NPCListener;
import dev.dpvb.survival.npc.NPCManager;
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
        // Add Commands
        getCommand("test").setExecutor(new TestCommand());
        // Register Listener
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        // Load NPCs
        Bukkit.getScheduler().runTaskLater(this, NPCManager.getInstance()::loadNPCs, 5);
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
    }
}
