package dev.dpvb.survive.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.survive.mongo.models.FacedBlock;
import org.bson.Document;

// TODO do we need to store time-to-hack?

/**
 * Stores position data for the natural spawning of hacked chests.
 */
public class HackedChestService extends MongoService<FacedBlock> {
    public HackedChestService(MongoDatabase database) {
        super(database, "hacked-chests", FacedBlock.class);
    }

    public void create(FacedBlock chestData) {
        collection.insertOne(chestData);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }
}
