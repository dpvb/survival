package dev.dpvb.survive.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.survive.mongo.models.FacedBlock;
import org.bson.Document;

/**
 * Stores position data for the natural spawning of hackable crates.
 */
public class HackableCrateSpawnsService extends MongoService<FacedBlock> {
    public HackableCrateSpawnsService(MongoDatabase database) {
        super(database, "hackable-crate-spawns", FacedBlock.class);
    }

    public void create(FacedBlock chestData) {
        collection.insertOne(chestData);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }
}
