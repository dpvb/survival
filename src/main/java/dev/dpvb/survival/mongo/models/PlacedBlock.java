package dev.dpvb.survival.mongo.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public final class PlacedBlock {

    @BsonId
    private Long blockKey;
    private Integer health;
    private Integer tier;

    public PlacedBlock(final Long blockKey, final Integer health, final Integer tier) {
        this.blockKey = blockKey;
        this.health = health;
        this.tier = tier;
    }

    public Long getBlockKey() {
        return blockKey;
    }

    public void setBlockKey(Long blockKey) {
        this.blockKey = blockKey;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }
}
