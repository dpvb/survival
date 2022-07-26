package dev.dpvb.survival.mongo.models;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerStorage {

    @BsonId
    private UUID uuid;
    private Map<Integer, byte[]> contents;

    public PlayerStorage() {

    }

    public PlayerStorage(UUID uuid, Map<Integer, byte[]> contents) {
        this.uuid = uuid;
        this.contents = contents;
    }

    public UUID getId() {
        return uuid;
    }

    public void setId(UUID uuid) {
        this.uuid = uuid;
    }

    public Map<Integer, byte[]> getContents() {
        return contents;
    }

    public void setContents(Map<Integer, byte[]> contents) {
        this.contents = contents;
    }
}
