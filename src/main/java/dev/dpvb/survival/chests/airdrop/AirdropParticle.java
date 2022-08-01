package dev.dpvb.survival.chests.airdrop;

import com.destroystokyo.paper.ParticleBuilder;

public class AirdropParticle {

    private final double xOff;
    private final double yOff;
    private final double zOff;
    private final ParticleBuilder particleBuilder;

    public AirdropParticle(double xOff, double yOff, double zOff, ParticleBuilder particleBuilder) {
        this.xOff = xOff;
        this.yOff = yOff;
        this.zOff = zOff;
        this.particleBuilder = particleBuilder;
    }

    public double getXOff() {
        return xOff;
    }

    public double getYOff() {
        return yOff;
    }

    public double getZOff() {
        return zOff;
    }

    public ParticleBuilder getParticleBuilder() {
        return particleBuilder;
    }
}
