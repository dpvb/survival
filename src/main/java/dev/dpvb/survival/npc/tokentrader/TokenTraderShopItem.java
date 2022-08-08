package dev.dpvb.survival.npc.tokentrader;

import org.bukkit.Material;

import java.util.Set;

public class TokenTraderShopItem {

    private final String shopItemName;
    private final int cost;
    private final Material displayMaterial;
    public static final Set<String> VALID_ITEMS = Set.of(
            "airdrop"
    );

    public TokenTraderShopItem(String shopItemName, int cost, Material displayMaterial) {
        this.shopItemName = shopItemName;
        this.cost = cost;
        this.displayMaterial = displayMaterial;
    }

    public String getShopItemName() {
        return shopItemName;
    }

    public int getCost() {
        return cost;
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }
}
