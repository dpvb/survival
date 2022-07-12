package dev.dpvb.survival.util.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemGenerator {

    private ItemStack item;
    private ItemMeta meta;
    private String displayName;
    private int amount;

    public ItemGenerator(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemGenerator setMaterial(Material material) {
        item.setType(material);
        return this;
    }

    public ItemGenerator setDisplayName(String displayName) {
        setDisplayName(Component.text(displayName));
        return this;
    }

    public ItemGenerator setDisplayName(Component displayName) {
        meta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemGenerator addLoreLine(String loreText) {
        if (!meta.hasLore()) {
            meta.lore(List.of(Component.text(loreText)));
        } else {
            //noinspection ConstantConditions
            meta.lore().add(Component.text(loreText));
        }
        return this;
    }

    public ItemGenerator setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return new ItemStack(item);
    }

}
