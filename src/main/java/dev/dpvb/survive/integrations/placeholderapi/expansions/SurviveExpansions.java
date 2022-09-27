package dev.dpvb.survive.integrations.placeholderapi.expansions;

import dev.dpvb.survive.Survive;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SurviveExpansions extends PlaceholderExpansion {
    final Map<String, Function<OfflinePlayer, @Nullable String>> protoExpansions = new HashMap<>();
    final Survive plugin = Survive.getInstance();

    public void addProtoExpansion(String params, Function<OfflinePlayer, @Nullable String> expansion) {
        protoExpansions.put(params, expansion);
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        final var proto = protoExpansions.get(params);
        if (proto == null) return null;
        return proto.apply(player);
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return onRequest(player, params);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "survive";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @NotNull String getName() {
        return plugin.getName() + " " + protoExpansions.keySet().stream().collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public @NotNull String getRequiredPlugin() {
        return plugin.getName();
    }

    @Override
    public boolean persist() {
        return true;
    }
}
