package dev.dpvb.survival.npc.upgrader;

import org.bukkit.Material;

public class UpgradeCost {

    private Material to;
    private int price;

    public UpgradeCost(Material to, int price) {
        this.to = to;
        this.price = price;
    }

    public Material getTo() {
        return to;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "UpgradeCost{" +
                "to=" + to +
                ", price=" + price +
                '}';
    }
}
