package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.dpvb.survival.mongo.models.PlayerStorage;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class PlayerStorageService {

    private MongoCollection<PlayerStorage> playerStorageCollection;

    public PlayerStorageService(MongoDatabase database) {
        playerStorageCollection = database.getCollection("player-storage", PlayerStorage.class);
    }

    public void replace(PlayerStorage playerStorage) {
        Bson query = Filters.eq(playerStorage.getId());
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        playerStorageCollection.replaceOne(query, playerStorage, options);
    }

    public List<PlayerStorage> getAll() {
        List<PlayerStorage> list = new ArrayList<>();
        final MongoCursor<PlayerStorage> cursor = playerStorageCollection.find().cursor();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }

        return list;
    }

}
