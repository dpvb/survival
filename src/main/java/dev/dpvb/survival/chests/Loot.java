package dev.dpvb.survival.chests;

import org.bukkit.inventory.ItemStack;

public class Loot {

    private ItemStack item;
    private double chance;

    public Loot(ItemStack item, double chance) {
        this.item = item;
        this.chance = chance;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getChance() {
        return chance;
    }
}
