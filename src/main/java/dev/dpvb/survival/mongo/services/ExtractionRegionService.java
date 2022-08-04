package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.mongo.models.ExtractionRegion;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ExtractionRegionService {

    private MongoCollection<ExtractionRegion> collection;

    public ExtractionRegionService(MongoDatabase database) {
        collection = database.getCollection("extraction-regions", ExtractionRegion.class);
    }

    public void create(ExtractionRegion region) {
        collection.insertOne(region);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

    public List<ExtractionRegion> getAll() {
        final List<ExtractionRegion> list = new ArrayList<>();
        final MongoCursor<ExtractionRegion> cursor = collection.find().cursor();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }

        return list;
    }
}
