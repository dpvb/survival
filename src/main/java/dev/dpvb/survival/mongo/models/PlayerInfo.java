package dev.dpvb.survival.mongo.models;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.UUID;

public class PlayerInfo {

    @BsonId
    private UUID id;
    private Integer tokens;
    private Integer kills;
    private Integer deaths;

    public PlayerInfo() {

    }

    public PlayerInfo(final UUID id, final Integer tokens) {
        this.id = id;
        this.tokens = tokens;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public Integer getTokens() {
        return tokens;
    }

    public void setTokens(Integer tokens) {
        this.tokens = tokens;
    }

    public Integer getKills() {
        return kills;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }
}
