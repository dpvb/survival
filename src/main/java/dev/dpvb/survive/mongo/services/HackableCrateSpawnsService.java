package dev.dpvb.survive.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.survive.mongo.models.FacedBlock;
import dev.dpvb.survive.mongo.models.SpawnLocation;
import org.bson.Document;

/**
 * Stores position data for the natural spawning of hackable crates.
 */
public class HackableCrateSpawnsService extends MongoService<SpawnLocation> {
    public HackableCrateSpawnsService(MongoDatabase database) {
        super(database, "hackable-crate-spawns", SpawnLocation.class);
    }

    public void create(SpawnLocation location) {
        collection.insertOne(location);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }
}
