package dev.dpvb.survival.chests;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public enum ChestTier implements LootSource {
    ONE(Material.CHEST),
    TWO(Material.ENDER_CHEST),
    THREE(Material.CHEST),
    ;

    private final Material chestMaterial;
    private final Set<Loot> lootTable = new HashSet<>();

    ChestTier(Material chestMaterial) {
        this.chestMaterial = chestMaterial;
        fillLootTable();
    }

    private void fillLootTable() {
        // Format (in config.yml)
        // ...
        // loot:
        //   tier-{tier}:
        //     {item_material}:
        //       chance: {chance}
        //       amount: {amount}
        // ...
        // amount is optional, if not specified, it will be 1.
        LootSource.loadFromLoot("tier-" + name().toLowerCase(), lootTable);
    }

    @Override
    public @NotNull List<ItemStack> generateLoot() {
        final List<ItemStack> items = new LinkedList<>();

        for (Loot loot : lootTable) {
            double randDouble = ThreadLocalRandom.current().nextDouble();
            if (randDouble <= loot.chance()) {
                items.add(loot.item());
            }
        }

        return items;
    }

    @Deprecated
    public static ChestTier getTier(Material material) { // What about three?
        return switch (material) {
            case CHEST -> ONE;
            case ENDER_CHEST -> TWO;
            default -> throw(new IllegalStateException());
        };
    }

    public Material getChestMaterial() {
        return chestMaterial;
    }

    public Set<Loot> getLootTable() {
        return lootTable;
    }
}
