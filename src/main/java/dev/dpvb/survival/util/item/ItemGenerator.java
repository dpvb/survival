package dev.dpvb.survival.util.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemGenerator {

    private Component displayName;
    private List<Component> lore;

    public ItemGenerator() {
        lore = new ArrayList<>();
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


    public ItemStack build(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        Optional.ofNullable(displayName).ifPresent(meta::displayName);

        if (lore.size() != 0) {
            meta.lore(lore);
        }

        item.setItemMeta(meta);
        return new ItemStack(item);
    }

}
