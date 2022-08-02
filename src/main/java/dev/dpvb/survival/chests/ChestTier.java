package dev.dpvb.survival.chests;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.util.item.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public enum ChestTier {
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

        // (Try to) get section for this tier
        final var tierSection = Survival.Configuration.getLootSection()
                .getConfigurationSection("tier-" + name().toLowerCase());
        if (tierSection == null) {
            // fail gracefully
            return;
        }
        // Iterate keys (material names)
        for (String materialName : tierSection.getKeys(false)) {
            // Attempt to parse Material
            Material material;
            try {
                material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe("An upgrade configuration is incorrect: " + materialName + " is not a valid Material.");
                continue; // skip item and continue parsing
            }

            // Read chance value
            double chance = tierSection.getDouble(materialName + ".chance");

            // Try-read amount; default gracefully to 1
            int amount = tierSection.getInt(materialName + ".amount", 1);

            // Create appropriate ItemStack
            final var item = new ItemGenerator()
                    .setAmount(amount)
                    .build(material);

            // Create Loot object and add it to the collection.
            lootTable.add(new Loot(item, chance));
        }
    }

    public List<ItemStack> generateLoot() {
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
