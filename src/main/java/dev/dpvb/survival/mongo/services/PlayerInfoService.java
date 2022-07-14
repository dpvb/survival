package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.dpvb.survival.mongo.models.PlayerInfo;

import java.util.UUID;

public class PlayerInfoService {

    private MongoCollection<PlayerInfo> playerInfoCollection;

    public PlayerInfoService(MongoDatabase database) {
        playerInfoCollection = database.getCollection("player-info", PlayerInfo.class);
    }

    public void create(PlayerInfo playerInfo) {
        playerInfoCollection.insertOne(playerInfo);
    }

    public PlayerInfo get(UUID id) {
        return playerInfoCollection.find(Filters.eq(id)).first();
    }

}
