package dev.dpvb.survival.util.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemGenerator {

    private Component displayName;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;
    private int amount = 1;
    private boolean hideEnchantments = false;

    public ItemGenerator() {
        lore = new ArrayList<>();
        enchantments = new HashMap<>();
    }

    public ItemGenerator setDisplayName(String displayName) {
        return setDisplayName(Component.text(displayName));
    }

    public ItemGenerator setDisplayName(Component displayName) {
        this.displayName = displayName.decoration(TextDecoration.ITALIC, false);
        return this;
    }

    public ItemGenerator addLoreLine(String loreText) {
        return addLoreLine(Component.text(loreText));
    }

    public ItemGenerator addLoreLine(Component text) {
        text = text.decoration(TextDecoration.ITALIC, false);
        lore.add(text);
        return this;
    }

    public ItemGenerator addEnchantment(Enchantment enchantment, Integer level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemGenerator hideEnchantments(boolean hide) {
        hideEnchantments = hide;
        return this;
    }

    public ItemGenerator setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build(Material material) {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        Optional.ofNullable(displayName).ifPresent(meta::displayName);

        if (lore.size() != 0) {
            meta.lore(lore);
        }

        if (hideEnchantments) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        item.addUnsafeEnchantments(enchantments);
        item.setAmount(amount);
        return new ItemStack(item);
    }

}
