package dev.dpvb.survival.npc;

import dev.dpvb.survival.Survival;
import dev.dpvb.survival.npc.enchanting.AdvancedEnchanterNPC;
import dev.dpvb.survival.npc.enchanting.BasicEnchanterNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class NPCManager {

    private static NPCManager instance;
    private Set<AbstractNPC> npcs;

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
        BasicEnchanterNPC.loadEnchantments(enchConfig.getConfigurationSection("basic"));
        AdvancedEnchanterNPC.loadEnchantments(enchConfig.getConfigurationSection("advanced"));

        // Create AbstractNPC instances.
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            switch (npc.getName().split(" ")[0]) {
                case "b-ench" -> npcs.add(new BasicEnchanterNPC(npc));
                case "a-ench" -> npcs.add(new AdvancedEnchanterNPC(npc));
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

    public Set<AbstractNPC> getNPCs() {
        return npcs;
    }
}
