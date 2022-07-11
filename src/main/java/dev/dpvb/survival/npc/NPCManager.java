package dev.dpvb.survival.npc;

import dev.dpvb.survival.Survival;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
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
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            String npcName = npc.getName();
            String identifier = npcName.split(" ")[0];
            if (identifier.equals("b-ench")) {
                npcs.add(new BasicEnchanterNPC(npc));
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
