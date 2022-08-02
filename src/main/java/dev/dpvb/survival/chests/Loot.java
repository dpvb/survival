package dev.dpvb.survival.chests;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record Loot(@NotNull ItemStack item, double chance) {
}
