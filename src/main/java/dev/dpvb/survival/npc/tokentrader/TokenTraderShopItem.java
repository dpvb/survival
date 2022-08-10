package dev.dpvb.survival.npc.tokentrader;

import org.bukkit.Material;

import java.util.Set;

public record TokenTraderShopItem(String shopItemName, int cost, Material displayMaterial) {

    public static final Set<String> VALID_ITEMS = Set.of(
            "airdrop"
    );

}
