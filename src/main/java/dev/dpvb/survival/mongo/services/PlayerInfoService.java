package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.dpvb.survival.mongo.models.PlayerInfo;
import org.bson.conversions.Bson;

import java.util.UUID;

public class PlayerInfoService extends MongoService<PlayerInfo> {

    public PlayerInfoService(MongoDatabase database) {
        super(database, "player-info", PlayerInfo.class);
    }

    public void create(PlayerInfo playerInfo) {
        collection.insertOne(playerInfo);
    }

    public void replace(PlayerInfo playerInfo) {
        Bson query = Filters.eq(playerInfo.getId());
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        collection.replaceOne(query, playerInfo, options);
    }

    public PlayerInfo get(UUID id) {
        return collection.find(Filters.eq(id)).first();
    }

}
