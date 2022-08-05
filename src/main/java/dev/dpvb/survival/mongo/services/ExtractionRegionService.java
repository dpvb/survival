package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.mongo.models.Region;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ExtractionRegionService {

    private MongoCollection<Region> collection;

    public ExtractionRegionService(MongoDatabase database) {
        collection = database.getCollection("extraction-regions", Region.class);
    }

    public void create(Region region) {
        collection.insertOne(region);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

    public List<Region> getAll() {
        final List<Region> list = new ArrayList<>();
        final MongoCursor<Region> cursor = collection.find().cursor();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }

        return list;
    }
}
