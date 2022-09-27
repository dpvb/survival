package dev.dpvb.survive.integrations.placeholderapi.expansions;

import dev.dpvb.survive.stats.PlayerInfoManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PointsExpansion implements ProtoExpansion {
    @Override
    public @Nullable String apply(@Nullable OfflinePlayer player) {
        if (player == null) return null;
        final UUID uuid = player.getUniqueId();
        final var manager = PlayerInfoManager.getInstance();
        if (manager.playerInfoExists(uuid)) return String.valueOf(manager.getTokens(uuid));
        return null;
    }
}
