package dev.dpvb.survive.npc.listeners;

import dev.dpvb.survive.npc.NPCManager;
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
