package dev.dpvb.survival.npc.enchanting;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EnchantmentCost {

    private final Enchantment enchantment;
    private final Map<Integer, Integer> levelToPrice;

    public EnchantmentCost(ConfigurationSection config) {
        levelToPrice = new HashMap<>();

        enchantment = Enchantment.getByKey(NamespacedKey.minecraft(config.getName()));

        Set<String> keys = config.getKeys(false);
        for (String key : keys) {
            levelToPrice.put(Integer.parseInt(key), config.getInt(key));
        }
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public Map<Integer, Integer> getLevelToPrice() {
        return levelToPrice;
    }

    @Override
    public String toString() {
        return "EC: " + enchantment.toString() + " " + levelToPrice.entrySet().stream().map(entry -> entry.getKey() + " " + entry.getValue()).collect(Collectors.joining());
    }
}
