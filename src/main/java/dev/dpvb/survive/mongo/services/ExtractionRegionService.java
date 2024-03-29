package dev.dpvb.survive.mongo.services;

import com.mongodb.client.MongoDatabase;
import dev.dpvb.survive.mongo.models.Region;
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
