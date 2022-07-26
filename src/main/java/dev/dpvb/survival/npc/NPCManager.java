package dev.dpvb.survival.npc;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.npc.enchanting.AdvancedEnchanterNPC;
import dev.dpvb.survival.npc.enchanting.BasicEnchanterNPC;
import dev.dpvb.survival.npc.enchanting.EnchantmentCost;
import dev.dpvb.survival.npc.enchanting.ItemTypes;
import dev.dpvb.survival.npc.storage.StorageNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NPCManager {

    private static NPCManager instance;
    private Set<AbstractNPC> npcs;
    private Map<ItemTypes, Set<EnchantmentCost>> basicEnchantments;
    private Map<ItemTypes, Set<EnchantmentCost>> advancedEnchantments;

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

        // Create AbstractNPC instances.
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            switch (npc.getName().split(" ")[0]) {
                case "b-ench" -> npcs.add(new BasicEnchanterNPC(npc));
                case "a-ench" -> npcs.add(new AdvancedEnchanterNPC(npc));
                case "store" -> npcs.add(new StorageNPC(npc));
            }
        }

        Bukkit.getLogger().info("Loaded " + npcs.size() + " Survival NPCs.");
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

    public Map<ItemTypes, Set<EnchantmentCost>> loadEnchantments(ConfigurationSection section) {
        Map<ItemTypes, Set<EnchantmentCost>> enchantments = new HashMap<>();
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

        return enchantments;
    }

    public Set<AbstractNPC> getNPCs() {
        return npcs;
    }

    public Map<ItemTypes, Set<EnchantmentCost>> getBasicEnchantments() {
        return basicEnchantments;
    }

    public Map<ItemTypes, Set<EnchantmentCost>> getAdvancedEnchantments() {
        return advancedEnchantments;
    }
}
