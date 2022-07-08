package dev.dpvb.survival.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.Survival;
import dev.dpvb.survival.mongo.models.PlacedBlock;
import dev.dpvb.survival.mongo.services.PlacedBlockService;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.w3c.dom.css.CSSUnknownRule;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoManager {

    private static MongoManager instance;
    private PlacedBlockService placedBlockService;

    private MongoManager() {
        String connectionString = Survival.Configuration.getMongoConnectionString()
                .replace("<username>:<password>",
                        Survival.Configuration.getMongoUsername() + ":" + Survival.Configuration.getMongoPassword());
        ConnectionString connString = new ConnectionString(connectionString);

        Bukkit.getLogger().info(connectionString);

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .codecRegistry(pojoCodecRegistry)
                .build();

        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase db = mongoClient.getDatabase("survival-db");

        Bukkit.getLogger().info("Database connected.");

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
