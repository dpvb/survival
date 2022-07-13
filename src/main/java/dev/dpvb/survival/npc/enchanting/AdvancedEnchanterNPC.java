package dev.dpvb.survival.npc.enchanting;

import dev.dpvb.survival.gui.InventoryWrapper;
import dev.dpvb.survival.npc.AbstractNPC;
import dev.dpvb.survival.npc.enchanting.menus.EnchantingInputMenu;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class AdvancedEnchanterNPC extends AbstractNPC {

    private static Map<ItemTypes, Set<EnchantmentCost>> enchantments;

    public AdvancedEnchanterNPC(Location location) {
        super(
                "a-ench " + new Random().nextInt(10000),
                ChatColor.translateAlternateColorCodes('&', "&d&lAdvanced Enchanter"),
                "advanced-enchanter",
                location
        );
    }

    public AdvancedEnchanterNPC(NPC npc) {
        super(npc);
    }

    @Override
    public void rightClickAction(Player clicker) {
        InventoryWrapper inputMenu = new EnchantingInputMenu(clicker, enchantments).register();
        clicker.openInventory(inputMenu.getInventory());
    }

    public static void loadEnchantments(ConfigurationSection section) {
        enchantments = new HashMap<>();
        Set<String> types = section.getKeys(false);
        for (String type : types) {
            ConfigurationSection typeSection = section.getConfigurationSection(type);
            if (typeSection == null) {
                continue;
            }

            ItemTypes itemType = ItemTypes.valueOf(type.toUpperCase());

            Set<EnchantmentCost> enchantmentCosts = new HashSet<>();
            Set<String> enchantNames = typeSection.getKeys(false);
            for (String enchantName : enchantNames) {
                ConfigurationSection enchantSection = typeSection.getConfigurationSection(enchantName);
                if (enchantSection == null) {
                    continue;
                }

                EnchantmentCost ec = new EnchantmentCost(enchantSection);
                enchantmentCosts.add(ec);
            }
            enchantments.put(itemType, enchantmentCosts);
        }
    }
}
