package dev.dpvb.survival.chests.airdrop;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class AirdropAnimation extends BukkitRunnable {

    private final Location location;
    private final int height;
    private final long length;
    private final Location top;
    private final World world;
    private final Map<Long, List<AirdropParticle>> particleSpawnMap;
    private long counter;
    private final AirdropManager airdropManager;

    public AirdropAnimation(Location location) {
        this.location = location;
        airdropManager = AirdropManager.getInstance();
        this.height = airdropManager.getAirdropAnimationHeight();
        this.length = airdropManager.getAirdropAnimationLength();
        this.top = location.clone().add(0.5, height, 0.5);
        this.world = location.getWorld();
        this.particleSpawnMap = airdropManager.getParticleSpawnMap();
        this.counter = 0;
    }

    @Override
    public void run() {
        List<AirdropParticle> airdropParticles = particleSpawnMap.get(counter);
        Location currLoc = top.clone().add(0, ((double) counter / length) * -height, 0);
        for (AirdropParticle airdropParticle : airdropParticles) {
            Location spawnLoc = top.clone().add(airdropParticle.getXOff(), airdropParticle.getYOff(), airdropParticle.getZOff());
            airdropParticle.getParticleBuilder()
                    .location(spawnLoc)
                    .receivers(400, 200)
                    .spawn();
        }

        if (counter % 10 == 0) {
            world.playSound(currLoc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 100f, 1f);
        }

        counter++;
        if (counter == length) {
            new ParticleBuilder(Particle.EXPLOSION_HUGE)
                    .location(location)
                    .count(10)
                    .receivers(100)
                    .spawn();
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 100f, 1f);
            world.strikeLightningEffect(location);
            airdropManager.createAirdropChest(location);
            cancel();
        }
    }
}
