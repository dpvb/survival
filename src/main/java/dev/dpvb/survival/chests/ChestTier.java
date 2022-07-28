package dev.dpvb.survival.chests;

import org.bukkit.Material;

public enum ChestTier {

    ONE(Material.CHEST),
    TWO(Material.ENDER_CHEST);

    private final Material chestMaterial;

    ChestTier(Material chestMaterial) {
        this.chestMaterial = chestMaterial;
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
}
