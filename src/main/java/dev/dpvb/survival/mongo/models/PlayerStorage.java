package dev.dpvb.survival.mongo.models;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused") // Mongo model
public class PlayerStorage {

    @BsonId
    private UUID uuid;
    private Map<String, byte[]> contents;

    public PlayerStorage() {

    }

    public PlayerStorage(UUID uuid, Map<String, byte[]> contents) {
        this.uuid = uuid;
        this.contents = contents;
    }

    public UUID getId() {
        return uuid;
    }

    public void setId(UUID uuid) {
        this.uuid = uuid;
    }

    public Map<String, byte[]> getContents() {
        return contents;
    }

    public void setContents(Map<String, byte[]> contents) {
        this.contents = contents;
    }
}
