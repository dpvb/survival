package dev.dpvb.survival;

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
    }

    private void setupConfigFile() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Configuration.config = getConfig();
    }

    public static Survival getInstance() {
        return instance;
    }

    public static class Configuration {
        private static FileConfiguration config;
    }
}
