package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.mongo.models.SpawnLocation;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class SpawnLocationService {

    private MongoCollection<SpawnLocation> collection;

    public SpawnLocationService(MongoDatabase database) {
        collection = database.getCollection("spawn-locations", SpawnLocation.class);
    }

    public void create(SpawnLocation region) {
        collection.insertOne(region);
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

    public List<SpawnLocation> getAll() {
        final List<SpawnLocation> list = new ArrayList<>();
        final MongoCursor<SpawnLocation> cursor = collection.find().cursor();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }

        return list;
    }

}
