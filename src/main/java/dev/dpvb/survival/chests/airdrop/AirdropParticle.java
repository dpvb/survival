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

    public double getxOff() {
        return xOff;
    }

    public double getyOff() {
        return yOff;
    }

    public double getzOff() {
        return zOff;
    }

    public ParticleBuilder getParticleBuilder() {
        return particleBuilder;
    }
}
