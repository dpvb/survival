package dev.dpvb.survival.npc;

import net.citizensnpcs.api.npc.NPC;
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
