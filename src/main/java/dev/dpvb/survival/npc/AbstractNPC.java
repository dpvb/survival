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

    private final String name;
    private final String displayName;
    private final String skinConfigKey;
    private final String skinTexture;
    private final String skinSignature;
    private NPC npc;

    public AbstractNPC(String name, String displayName, String skinConfigKey, Location location) {
        this.name = name;
        this.displayName = displayName;
        this.skinConfigKey = skinConfigKey;
        this.skinTexture = Survival.Configuration.getNPCSkinSection().getString(skinConfigKey + ".texture");
        this.skinSignature = Survival.Configuration.getNPCSkinSection().getString(skinConfigKey + ".signature");
        setup(location);
    }

    private void setup(Location location) {
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        // Set Skin
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                skinConfigKey,
                skinSignature,
                skinTexture
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

    public abstract void rightClickAction(Player clicker);

    public NPC getNPC() {
        return npc;
    }
}
