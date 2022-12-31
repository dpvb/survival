package dev.dpvb.survive.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.dpvb.survive.Survive;
import dev.dpvb.survive.mongo.services.*;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoManager {

    private static MongoManager instance;
    private final PlayerInfoService playerInfoService;
    private final PlayerStorageService playerStorageService;
    private final ChestDataService chestDataService;
    private final HackableCrateSpawnsService hackableCrateSpawnsService;
    private final ExtractionRegionService extractionRegionService;
    private final SpawnLocationService spawnLocationService;

    private MongoManager() {
        ConnectionString connString = new ConnectionString(
                Survive.Configuration.getMongoConnectionString().replace(
                        "<username>:<password>",
                        Survive.Configuration.getMongoUsername() + ":" + Survive.Configuration.getMongoPassword())
        );

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .codecRegistry(pojoCodecRegistry)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        //noinspection resource
        MongoClient mongoClient = MongoClients.create(clientSettings); // TODO: consider starting this client on-demand
        MongoDatabase db = mongoClient.getDatabase("survival-db"); // TODO: Rename in future

        Bukkit.getLogger().info("Database connected.");

        playerInfoService = new PlayerInfoService(db);
        playerStorageService = new PlayerStorageService(db);
        chestDataService = new ChestDataService(db);
        hackableCrateSpawnsService = new HackableCrateSpawnsService(db);
        extractionRegionService = new ExtractionRegionService(db);
        spawnLocationService = new SpawnLocationService(db);
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

    public ChestDataService getChestDataService() {
        return chestDataService;
    }

    public HackableCrateSpawnsService getHackableCrateSpawnsService() {
        return hackableCrateSpawnsService;
    }

    public ExtractionRegionService getExtractionRegionService() {
        return extractionRegionService;
    }

    public SpawnLocationService getSpawnLocationService() {
        return spawnLocationService;
    }
}
