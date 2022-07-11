package dev.dpvb.survival.npc;

import net.citizensnpcs.api.npc.NPC;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class NPCManager {

    private static NPCManager instance;
    private Set<BasicEnchanterNPC> npcs;

    private NPCManager() {
        npcs = new HashSet<>();
    }

    public static NPCManager getInstance() {
        if (instance == null) {
            instance = new NPCManager();
        }

        return instance;
    }

    public void addNPC(BasicEnchanterNPC npc) {
        npcs.add(npc);
    }

    @Nullable
    public BasicEnchanterNPC getNPC(NPC npc) {
        for (BasicEnchanterNPC basicEnchanterNPC : npcs) {
            if (npc.equals(basicEnchanterNPC.getNPC())) {
                return basicEnchanterNPC;
            }
        }

        return null;
    }

    public Set<BasicEnchanterNPC> getNPCs() {
        return npcs;
    }
}
