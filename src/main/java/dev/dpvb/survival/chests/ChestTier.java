package dev.dpvb.survival.chests;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.util.item.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public enum ChestTier {

    ONE(Material.CHEST),
    TWO(Material.ENDER_CHEST);

    private final Material chestMaterial;
    private final Set<Loot> lootTable;
    private final Random random;

    ChestTier(Material chestMaterial) {
        this.chestMaterial = chestMaterial;
        lootTable = new HashSet<>();
        random = new Random();
        fillLootTable();
    }

    private void fillLootTable() {
        ConfigurationSection section = Survival.Configuration.getLootSection()
                .getConfigurationSection("tier-" + name().toLowerCase());
        Set<String> materialNames = section.getKeys(false);
        for (String materialName : materialNames) {
            // Attempt to parse Material Name
            Material material = Material.valueOf(materialName.toUpperCase());
            if (material == null) {
                Bukkit.getLogger().severe("Upgrade configuration is incorrect. " + materialName + " is an invalid Material.");
            }

            // Get the chance from the entry
            double chance = section.getDouble(materialName + ".chance");

            // Check if an Amount field exists
            int amount = section.getInt(materialName + ".amount", 1);

            // Create ItemStack
            ItemStack item = new ItemGenerator()
                    .setAmount(amount)
                    .build(material);

            // Create Loot object and add it to Loot Set.
            lootTable.add(new Loot(item, chance));
        }
    }

    public Set<ItemStack> generateLoot() {
        Set<ItemStack> items = new HashSet<>();

        for (Loot loot : lootTable) {
            double randDouble = random.nextDouble();
            if (randDouble <= loot.getChance()) {
                items.add(loot.getItem());
            }
        }

        return items;
    }

    public static ChestTier getTier(Material material) {
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
