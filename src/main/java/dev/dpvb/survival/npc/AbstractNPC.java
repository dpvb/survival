package dev.dpvb.survival.npc;

import dev.dpvb.survival.Survival;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public abstract class AbstractNPC {

    private final NPC npc;

    /**
     * Use this constructor to create an NPC for the first time. This constructor should not be used on reinitialization.
     * @param name The name of the NPC
     * @param displayName The display name of the NPC
     * @param skinConfigKey The key of the configuration section for this NPC's skin.
     * @param location The {@link Location} to create the NPC
     */
    public AbstractNPC(String name, String displayName, String skinConfigKey, Location location) {
        // Create NPC
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        // Set Skin
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                skinConfigKey,
                Survival.Configuration.getNPCSkinSection().getString(skinConfigKey + ".signature"),
                Survival.Configuration.getNPCSkinSection().getString(skinConfigKey + ".texture")
        );

        // Setup Target Looking
        npc.getOrAddTrait(LookClose.class).toggle();

        // Setup Hologram Name
        npc.getOrAddTrait(HologramTrait.class)
                .addLine(displayName);

        // Remove the Nameplate
        npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        // Spawn the NPC
        npc.spawn(location);
    }

    /**
     * The constructor used to initialize an AbstractNPC object that has already been created.
     * This is used in conjunction with {@link NPCManager#loadNPCs()}
     * @param npc The {@link NPC} to initialize this class with.
     */
    public AbstractNPC(NPC npc) {
        this.npc = npc;
    }

    public abstract void rightClickAction(Player clicker);

    public NPC getNPC() {
        return npc;
    }
}
