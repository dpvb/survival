package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.mongo.models.Region;
import org.bson.Document;

public class ExtractionRegionService extends MongoService<Region> {

    public ExtractionRegionService(MongoDatabase database) {
        super(database, "extraction-regions", Region.class);
    }

    public void create(Region region) {
        collection.insertOne(region);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

}
