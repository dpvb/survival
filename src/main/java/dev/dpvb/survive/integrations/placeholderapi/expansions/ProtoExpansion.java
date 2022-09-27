package dev.dpvb.survive.integrations.placeholderapi.expansions;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public interface ProtoExpansion extends BiFunction<OfflinePlayer, @NotNull String, @Nullable String> {
    @Nullable String apply(OfflinePlayer player);

    default @Nullable String apply(OfflinePlayer player, @NotNull String params) {
        return apply(player);
    }
}
