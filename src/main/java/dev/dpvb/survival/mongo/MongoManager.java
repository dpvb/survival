package dev.dpvb.survival.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.Survival;
import dev.dpvb.survival.mongo.services.PlacedBlockService;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoManager {

    private static MongoManager instance;
    private PlacedBlockService placedBlockService;

    private MongoManager() {
        // Create Connection String
        String connectionString = Survival.Configuration.getMongoConnectionString()
                .replace("<username>:<password>",
                        Survival.Configuration.getMongoUsername() + ":" + Survival.Configuration.getMongoPassword());
        ConnectionString connString = new ConnectionString(connectionString);

        // Create Codec Registry for POJOs
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // Create Mongo Client Settings
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .codecRegistry(pojoCodecRegistry)
                .build();

        // Init the Connection and Get the Main Database
        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase db = mongoClient.getDatabase("survival-db");

        // Initialize Services
        placedBlockService = new PlacedBlockService(db);
    }

    public static MongoManager getInstance() {
        if (instance == null) {
            instance = new MongoManager();
        }

        return instance;
    }

    public PlacedBlockService getPlacedBlockService() {
        return placedBlockService;
    }
}
