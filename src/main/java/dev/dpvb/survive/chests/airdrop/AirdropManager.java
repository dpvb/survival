package dev.dpvb.survive.chests.airdrop;

import com.destroystokyo.paper.ParticleBuilder;
import dev.dpvb.survive.Survive;
import dev.dpvb.survive.chests.Loot;
import dev.dpvb.survive.chests.LootSource;
import dev.dpvb.survive.util.item.ItemGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class AirdropManager implements LootSource {

    private static AirdropManager instance;
    private final Map<Long, List<AirdropParticle>> particleSpawnMap = new HashMap<>();
    private final int airdropAnimationHeight = 200;
    private final int airdropAnimationLength = 200;
    private final List<AirdropChest> airdropChests = new ArrayList<>();
    private final AtomicBoolean clearing = new AtomicBoolean();
    private final Set<Loot> lootTable = new HashSet<>();
    private final ItemStack airdropItem = generateItem();

    private AirdropManager() {
        LootSource.loadFromLoot("airdrop", lootTable);
    }

    @Override
    public @NotNull List<ItemStack> generateLoot() {
        return GenerationFormula.DEFAULT.generateAs(lootTable, LinkedList::new);
    }

    public void loadAirdropAnimation() {
        final int radius = 1;
        final int numParticles = 35;

        for (int counter = 0; counter <= airdropAnimationLength; counter++) {
            List<AirdropParticle> list = new ArrayList<>();
            final double yOff = ((double) counter / airdropAnimationLength) * -airdropAnimationHeight;
            for (int i = 0; i < numParticles; i++) {
                final double cos = Math.cos(2 * Math.PI * i / numParticles);
                final double sin = Math.sin(2 * Math.PI * i / numParticles);
                final double xOff = (radius) * cos;
                final double zOff = (radius) * sin;
                final ParticleBuilder pb = new ParticleBuilder(Particle.FIREWORKS_SPARK)
                        .count(0)
                        .offset(.2 * cos, 0, .2 * sin);

                list.add(new AirdropParticle(xOff, yOff, zOff, pb));
            }

            list.add(new AirdropParticle(
                    0,
                    yOff,
                    0,
                    new ParticleBuilder(Particle.REDSTONE)
                        .data(new Particle.DustOptions(Color.fromRGB(255, 255 ,255), 1.0F))
                        .count(50)
            ));

            particleSpawnMap.put((long) counter, list);
        }
    }

    public void startAirdrop(Location location) {
        new AirdropAnimation(location).runTaskTimer(Survive.getInstance(), 0L, 1L);
    }

    protected void createAirdropChest(Location location) {
        airdropChests.add(new AirdropChest(location.getBlock()));
    }

    public void clearAirdrops() {
        clearing.set(true);
        for (AirdropChest airdropChest : airdropChests) {
            airdropChest.destroy();
        }
        clearing.set(false);
    }

    public void removeAirdropFromCache(AirdropChest airdropChest) {
        airdropChests.remove(airdropChest);
    }

    private ItemStack generateItem() {
        return new ItemGenerator()
                .setDisplayName(Component.text("AIRDROP").decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD))
                .addLoreLine(Component.text("Place in Arena to").decorate(TextDecoration.ITALIC).color(NamedTextColor.GRAY))
                .addLoreLine(Component.text("call an Airdrop.").decorate(TextDecoration.ITALIC).color(NamedTextColor.GRAY))
                .build(Material.TRIPWIRE_HOOK);
    }

    public ItemStack getAirdropItem() {
        return airdropItem;
    }

    public boolean isClearing() {
        return clearing.get();
    }

    public Map<Long, List<AirdropParticle>> getParticleSpawnMap() {
        return particleSpawnMap;
    }

    public int getAirdropAnimationHeight() {
        return airdropAnimationHeight;
    }

    public int getAirdropAnimationLength() {
        return airdropAnimationLength;
    }

    public static AirdropManager getInstance() {
        if (instance == null) {
            instance = new AirdropManager();
        }
        return instance;
    }
}
