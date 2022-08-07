package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.mongo.models.SpawnLocation;
import org.bson.Document;

public class SpawnLocationService extends MongoService<SpawnLocation> {

    public SpawnLocationService(MongoDatabase database) {
        super(database, "spawn-locations", SpawnLocation.class);
    }

    public void create(SpawnLocation region) {
        collection.insertOne(region);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

}
