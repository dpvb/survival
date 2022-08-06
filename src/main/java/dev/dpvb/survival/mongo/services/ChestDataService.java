package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.mongo.models.ChestData;
import org.bson.Document;

public class ChestDataService extends MongoService<ChestData> {

    public ChestDataService(MongoDatabase database) {
        super(database, "chest-data", ChestData.class);
    }

    public void create(ChestData chestData) {
        collection.insertOne(chestData);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

}
