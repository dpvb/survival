package dev.dpvb.survival.listeners;

import dev.dpvb.survival.npc.NPCManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class NPCListener implements Listener {

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC clickedNPC = event.getNPC();
        Optional.ofNullable(NPCManager.getInstance().getNPC(clickedNPC))
                .ifPresent(npc -> npc.rightClickAction(event.getClicker()));
    }

}
