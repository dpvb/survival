package dev.dpvb.survive.mongo.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

public class MongoService<T> {
    protected final MongoCollection<T> collection;

    MongoService(MongoDatabase database, String collection, Class<T> type) {
        this.collection = database.getCollection(collection, type);
    }

    public List<T> getAll() {
        final List<T> list = new ArrayList<>();
        try (final MongoCursor<T> cursor = collection.find().cursor()) {
            while (cursor.hasNext()) {
                list.add(cursor.next());
            }
        }
        return list;
    }

}
