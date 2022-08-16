package dev.dpvb.survive.mongo.services;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.dpvb.survive.mongo.models.PlayerStorage;
import org.bson.conversions.Bson;

public class PlayerStorageService extends MongoService<PlayerStorage> {

    public PlayerStorageService(MongoDatabase database) {
        super(database, "player-storage", PlayerStorage.class);
    }

    public void replace(PlayerStorage playerStorage) {
        Bson query = Filters.eq(playerStorage.getId());
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        collection.replaceOne(query, playerStorage, options);
    }

}
