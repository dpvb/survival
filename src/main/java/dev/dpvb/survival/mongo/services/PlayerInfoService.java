package dev.dpvb.survival.mongo.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import dev.dpvb.survival.mongo.models.PlayerInfo;
import dev.dpvb.survival.stats.PlayerInfoManager;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInfoService {

    private MongoCollection<PlayerInfo> playerInfoCollection;

    public PlayerInfoService(MongoDatabase database) {
        playerInfoCollection = database.getCollection("player-info", PlayerInfo.class);
    }

    public void create(PlayerInfo playerInfo) {
        playerInfoCollection.insertOne(playerInfo);
    }

    public void replace(PlayerInfo playerInfo) {
        Bson query = Filters.eq(playerInfo.getId());
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        playerInfoCollection.replaceOne(query, playerInfo, options);
    }

    public PlayerInfo get(UUID id) {
        return playerInfoCollection.find(Filters.eq(id)).first();
    }

    public List<PlayerInfo> getAll() {
        List<PlayerInfo> list = new ArrayList<>();
        final MongoCursor<PlayerInfo> cursor = playerInfoCollection.find().cursor();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return list;
    }

}
