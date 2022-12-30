package dev.dpvb.survive.chests.tiered;

import dev.dpvb.survive.chests.Loot;
import dev.dpvb.survive.chests.LootSource;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public enum ChestTier implements LootSource {
    ONE(Material.CHEST, Sound.BLOCK_WOOD_BREAK, ChatColor.GOLD, "I"),
    TWO(Material.ENDER_CHEST, Sound.BLOCK_STONE_BREAK, ChatColor.LIGHT_PURPLE, "II"),
    ;

    private final Material chestMaterial;
    private final Sound breakSound;
    private final ChatColor holoColor;
    private final String holoTier;
    private final Set<Loot> lootTable = new HashSet<>();

    ChestTier(Material chestMaterial, Sound breakSound, ChatColor holoColor, String holoTier) {
        this.chestMaterial = chestMaterial;
        this.breakSound = breakSound;
        this.holoColor = holoColor;
        this.holoTier = holoTier;
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
        return GenerationFormula.DEFAULT.generateAs(lootTable, LinkedList::new);
    }

    public Material getChestMaterial() {
        return chestMaterial;
    }

    public Sound getBreakSound() {
        return breakSound;
    }

    public Set<Loot> getLootTable() {
        return lootTable;
    }

    public ChatColor getHoloColor() {
        return holoColor;
    }

    public String getHoloTier() {
        return holoTier;
    }
}
