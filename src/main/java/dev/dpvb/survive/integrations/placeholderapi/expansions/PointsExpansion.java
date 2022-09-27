package dev.dpvb.survive.integrations.placeholderapi.expansions;

import dev.dpvb.survive.stats.PlayerInfoManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public class PointsExpansion implements Function<OfflinePlayer, @Nullable String> {
    @Override
    public @Nullable String apply(@Nullable OfflinePlayer player) {
        if (player == null) return null;
        final UUID uuid = player.getUniqueId();
        final var manager = PlayerInfoManager.getInstance();
        if (manager.playerInfoExists(uuid)) return String.valueOf(manager.getTokens(uuid));
        return null;
    }
}
