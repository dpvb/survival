package dev.dpvb.survive.npc.upgrader;

import org.bukkit.Material;

public record UpgradeCost(Material to, int price) {
    @Override
    public String toString() {
        return "UpgradeCost{" +
                "to=" + to +
                ", price=" + price +
                '}';
    }
}
