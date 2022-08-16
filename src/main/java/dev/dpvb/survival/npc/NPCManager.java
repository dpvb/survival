package dev.dpvb.survival.npc;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.npc.enchanting.AdvancedEnchanterNPC;
import dev.dpvb.survival.npc.enchanting.BasicEnchanterNPC;
import dev.dpvb.survival.npc.enchanting.EnchantmentCost;
import dev.dpvb.survival.npc.enchanting.EnchantableItemTypes;
import dev.dpvb.survival.npc.join.JoinNPC;
import dev.dpvb.survival.npc.storage.StorageNPC;
import dev.dpvb.survival.npc.tokentrader.TokenTraderNPC;
import dev.dpvb.survival.npc.tokentrader.TokenTraderShopItem;
import dev.dpvb.survival.npc.upgrader.UpgradeCost;
import dev.dpvb.survival.npc.upgrader.UpgradeNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NPCManager {

    private static NPCManager instance;
    private final Set<AbstractNPC> npcs;
    private Map<EnchantableItemTypes, Set<EnchantmentCost>> basicEnchantments;
    private Map<EnchantableItemTypes, Set<EnchantmentCost>> advancedEnchantments;
    private Map<Material, UpgradeCost> upgrades;
    private List<TokenTraderShopItem> tokenTraderItems;

    private NPCManager() {
        npcs = new HashSet<>();
    }

    public static NPCManager getInstance() {
        if (instance == null) {
            instance = new NPCManager();
        }

        return instance;
    }

    /**
     * Creates instances of the AbstractNPCs from the NPCs in the Citizens Registry.
     * Should only be calling this in {@link Survival#onEnable()}
     */
    public void loadNPCs() {
        // Load Enchantment Configuration for Enchantment NPCs
        ConfigurationSection enchConfig = Survival.Configuration.getEnchantingSection();

        basicEnchantments = loadEnchantments(enchConfig.getConfigurationSection("basic"));
        advancedEnchantments = loadEnchantments(enchConfig.getConfigurationSection("advanced"));

        // Load Upgrade Configuration for Upgrade NPCs
        upgrades = loadUpgrades(Survival.Configuration.getUpgradingSection());

        // Load TokenTrader Configuration for TokenTrader NPCs
        tokenTraderItems = loadTokenTraderItems(Survival.Configuration.getTokenTraderSection());

        // Create AbstractNPC instances.
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            switch (npc.getName().split(" ")[0]) {
                case "b-ench" -> npcs.add(new BasicEnchanterNPC(npc));
                case "a-ench" -> npcs.add(new AdvancedEnchanterNPC(npc));
                case "store" -> npcs.add(new StorageNPC(npc));
                case "upgrade" -> npcs.add(new UpgradeNPC(npc));
                case "join" -> npcs.add(new JoinNPC(npc));
                case "t-trader" -> npcs.add(new TokenTraderNPC(npc));
            }
        }

        Bukkit.getLogger().info("Loaded " + npcs.size() + " Survive NPCs.");
    }

    public void addNPC(AbstractNPC npc) {
        npcs.add(npc);
    }

    @Nullable
    public AbstractNPC getNPC(NPC npc) {
        for (AbstractNPC abstractNPC : npcs) {
            if (npc.equals(abstractNPC.getNPC())) {
                return abstractNPC;
            }
        }

        return null;
    }

    private Map<EnchantableItemTypes, Set<EnchantmentCost>> loadEnchantments(ConfigurationSection section) {
        if (section == null) return Map.of(); // TODO: log?
        Map<EnchantableItemTypes, Set<EnchantmentCost>> enchantments = new HashMap<>();
        Set<String> types = section.getKeys(false);
        for (String type : types) {
            ConfigurationSection typeSection = section.getConfigurationSection(type);
            if (typeSection == null) {
                continue;
            }

            EnchantableItemTypes itemType = EnchantableItemTypes.valueOf(type.toUpperCase());

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

        return enchantments;
    }

    private Map<Material, UpgradeCost> loadUpgrades(ConfigurationSection section) {
        Map<Material, UpgradeCost> upgrades = new HashMap<>();
        for (String materialName : section.getKeys(false)) {
            Material material, toMaterial;
            try {
                material = Material.valueOf(materialName.toUpperCase());
                String toMaterialName = section.getString(materialName + ".to");
                if (toMaterialName == null) {
                    Bukkit.getLogger().warning("No upgrade 'to' material specified for " + materialName + ".");
                    continue;
                }
                toMaterial = Material.valueOf(toMaterialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe("Upgrade configuration is incorrect. " + materialName + " is an invalid Material.");
                Bukkit.getLogger().severe(e::getMessage);
                continue;
            }

            int price = section.getInt(materialName + ".price");
            upgrades.put(material, new UpgradeCost(toMaterial, price));
        }
        return upgrades;
    }

    private List<TokenTraderShopItem> loadTokenTraderItems(ConfigurationSection section) {
        List<TokenTraderShopItem> items = new ArrayList<>();
        for (String itemName : section.getKeys(false)) {
            if (!TokenTraderShopItem.VALID_ITEMS.contains(itemName)) {
                Bukkit.getLogger().severe("TokenTrader configuration is incorrect. " + itemName + " is not a valid shop item entry.");
            }

            String displayMaterialName = section.getString(itemName + ".display-material");
            if (displayMaterialName == null) {
                Bukkit.getLogger().warning("Missing display material for " + itemName + ". Skipping.");
                continue;
            }
            Material displayMaterial;
            try {
                displayMaterial = Material.valueOf(displayMaterialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe("TokenTrader configuration is incorrect. " + displayMaterialName + " is an invalid Material.");
                Bukkit.getLogger().severe(e::getMessage);
                continue;
            }

            int price = section.getInt(itemName + ".price");
            items.add(new TokenTraderShopItem(itemName, price, displayMaterial));
        }

        return items;
    }

    public Set<AbstractNPC> getNPCs() {
        return npcs;
    }

    public Map<EnchantableItemTypes, Set<EnchantmentCost>> getBasicEnchantments() {
        return basicEnchantments;
    }

    public Map<EnchantableItemTypes, Set<EnchantmentCost>> getAdvancedEnchantments() {
        return advancedEnchantments;
    }

    public Map<Material, UpgradeCost> getUpgrades() {
        return upgrades;
    }

    public List<TokenTraderShopItem> getTokenTraderItems() {
        return tokenTraderItems;
    }
}
