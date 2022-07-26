package dev.dpvb.survival.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survival.Survival;
import dev.dpvb.survival.mongo.services.PlayerInfoService;
import dev.dpvb.survival.mongo.services.PlayerStorageService;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoManager {

    private static MongoManager instance;
    private PlayerInfoService playerInfoService;
    private PlayerStorageService playerStorageService;

    private MongoManager() {
        ConnectionString connString = new ConnectionString(
                Survival.Configuration.getMongoConnectionString().replace(
                        "<username>:<password>",
                        Survival.Configuration.getMongoUsername() + ":" + Survival.Configuration.getMongoPassword())
        );

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .codecRegistry(pojoCodecRegistry)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase db = mongoClient.getDatabase("survival-db");

        Bukkit.getLogger().info("Database connected.");

        playerInfoService = new PlayerInfoService(db);
        playerStorageService = new PlayerStorageService(db);
    }

    public static MongoManager getInstance() {
        if (instance == null) {
            instance = new MongoManager();
        }

        return instance;
    }

    public PlayerInfoService getPlayerInfoService() {
        return playerInfoService;
    }

    public PlayerStorageService getPlayerStorageService() {
        return playerStorageService;
    }
}
