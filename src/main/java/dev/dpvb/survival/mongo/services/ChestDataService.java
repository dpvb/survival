package dev.dpvb.survival.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.mongo.models.ChestData;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ChestDataService {

    private MongoCollection<ChestData> chestDataCollection;

    public ChestDataService(MongoDatabase database) {
        chestDataCollection = database.getCollection("chest-data", ChestData.class);
    }

    public void create(ChestData chestData) {
        chestDataCollection.insertOne(chestData);
    }

    public void deleteAll() {
        chestDataCollection.deleteMany(new Document());
    }

    public List<ChestData> getAll() {
        final List<ChestData> list  = new ArrayList<>();
        final MongoCursor<ChestData> cursor = chestDataCollection.find().cursor();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }

        return list;
    }

}
