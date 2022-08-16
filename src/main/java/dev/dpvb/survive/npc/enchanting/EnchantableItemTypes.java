package dev.dpvb.survive.npc.enchanting;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public enum EnchantableItemTypes implements Predicate<Material> {

    SWORDS {
        @Override
        public boolean test(Material material) {
            return material == Material.WOODEN_SWORD ||
                    material == Material.STONE_SWORD ||
                    material == Material.IRON_SWORD ||
                    material == Material.DIAMOND_SWORD;
        }
    },
    AXES {
        @Override
        public boolean test(Material material) {
            return material == Material.WOODEN_AXE ||
                    material == Material.STONE_AXE ||
                    material == Material.IRON_AXE ||
                    material == Material.DIAMOND_AXE;
        }
    },
    BOW {
        @Override
        public boolean test(Material material) {
            return material == Material.BOW;
        }
    },
    CROSSBOW {
        @Override
        public boolean test(Material material) {
            return material == Material.CROSSBOW;
        }
    },
    TRIDENT {
        @Override
        public boolean test(Material material) {
            return material == Material.TRIDENT;
        }
    },
    HELMETS {
        @Override
        public boolean test(Material material) {
            return material == Material.LEATHER_HELMET ||
                    material == Material.CHAINMAIL_HELMET ||
                    material == Material.IRON_HELMET ||
                    material == Material.DIAMOND_HELMET;
        }
    },
    CHESTPLATES {
        @Override
        public boolean test(Material material) {
            return material == Material.LEATHER_CHESTPLATE ||
                    material == Material.CHAINMAIL_CHESTPLATE ||
                    material == Material.IRON_CHESTPLATE ||
                    material == Material.DIAMOND_CHESTPLATE;
        }
    },
    LEGGINGS {
        @Override
        public boolean test(Material material) {
            return material == Material.LEATHER_LEGGINGS ||
                    material == Material.CHAINMAIL_LEGGINGS ||
                    material == Material.IRON_LEGGINGS ||
                    material == Material.DIAMOND_LEGGINGS;

        }
    },
    BOOTS {
        @Override
        public boolean test(Material material) {
            return material == Material.LEATHER_BOOTS ||
                    material == Material.CHAINMAIL_BOOTS ||
                    material == Material.IRON_BOOTS ||
                    material == Material.DIAMOND_BOOTS;
        }
    };

    @Nullable
    public static EnchantableItemTypes getItemType(Material material) {
        for (EnchantableItemTypes type : EnchantableItemTypes.values()) {
            if (type.test(material)) {
                return type;
            }
        }

        return null;
    }

}
