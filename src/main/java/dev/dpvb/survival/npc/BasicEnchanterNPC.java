package dev.dpvb.survival.npc;

import dev.dpvb.survival.Survival;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BasicEnchanterNPC {

    private static final String name = "[NPC] b-ench";
    private final String displayName;
    private final Location location;
    private final String skinConfigKey;
    private final String skinTexture;
    private final String skinSignature;
    private NPC npc;

    public BasicEnchanterNPC(Location location) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', "&d&lBasic Enchanter");
        this.location = location;
        this.skinConfigKey = "basic-enchanter";
        this.skinSignature = Survival.Configuration.getNPCSkinSection().getString(skinConfigKey + ".signature");
        this.skinTexture = Survival.Configuration.getNPCSkinSection().getString(skinConfigKey + ".texture");
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

    public NPC getNPC() {
        return npc;
    }

    public void rightClickAction(Player clicker) {
        clicker.sendMessage("This is a test.");
    }
}
