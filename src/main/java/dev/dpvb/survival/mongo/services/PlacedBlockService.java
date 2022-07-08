package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.dpvb.survival.mongo.models.PlacedBlock;
import org.jetbrains.annotations.Nullable;

public class PlacedBlockService {

    private MongoCollection<PlacedBlock> pbCollection;

    public PlacedBlockService(MongoDatabase database) {
        pbCollection = database.getCollection("placed-blocks", PlacedBlock.class);
    }

    @Nullable
    public PlacedBlock get(long id) {
        return pbCollection.find(Filters.eq(id)).first();
    }

    public void create(PlacedBlock placedBlock) {
        pbCollection.insertOne(placedBlock);
    }

}
