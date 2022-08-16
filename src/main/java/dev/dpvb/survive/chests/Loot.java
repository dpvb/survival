package dev.dpvb.survive.chests;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record Loot(@NotNull ItemStack item, double chance) {
}
