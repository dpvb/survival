package dev.dpvb.survive.integrations.placeholderapi;

import org.bukkit.Bukkit;

public class PlaceholderAPIHook {
    public PlaceholderAPIHook() throws IllegalStateException {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            throw new IllegalStateException("PlaceholderAPI is not available.");
        }
        registerExpansions();
    }

    private void registerExpansions() {
        final var surviveExpansions = new dev.dpvb.survive.integrations.placeholderapi.expansions.SurviveExpansions();
        surviveExpansions.addProtoExpansion("points", new dev.dpvb.survive.integrations.placeholderapi.expansions.PointsExpansion());
        surviveExpansions.register();
    }
}
