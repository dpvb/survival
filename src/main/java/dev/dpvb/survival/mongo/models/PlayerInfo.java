package dev.dpvb.survival.mongo.models;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.UUID;

public class PlayerInfo {

    @BsonId
    private UUID id;
    private Integer tokens;

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
}
