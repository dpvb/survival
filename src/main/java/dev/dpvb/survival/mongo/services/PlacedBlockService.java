package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.mongo.models.PlacedBlock;

public class PlacedBlockService {

    private MongoCollection<PlacedBlock> pbCollection;

    public PlacedBlockService(MongoDatabase database) {
        pbCollection = database.getCollection("placed-blocks", PlacedBlock.class);
    }

    public void post(PlacedBlock placedBlock) {
        pbCollection.insertOne(placedBlock);
    }

}
