package dev.dpvb.survival.npc.storage;

import dev.dpvb.survival.mongo.MongoManager;
import dev.dpvb.survival.mongo.models.PlayerStorage;
import dev.dpvb.survival.mongo.services.PlayerStorageService;

import java.util.*;

public class StorageManager {

    private static StorageManager instance;

    private Map<UUID, PlayerStorage> storageMap;

    private StorageManager() {
        storageMap = new HashMap<>();
    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }

        return instance;
    }

    public void load() {
        List<PlayerStorage> all = MongoManager.getInstance().getPlayerStorageService().getAll();
        for (PlayerStorage storage : all) {
            storageMap.put(storage.getId(), storage);
        }
    }

    public void save() {
        final PlayerStorageService pss = MongoManager.getInstance().getPlayerStorageService();
        for (PlayerStorage storage : storageMap.values()) {
            pss.replace(storage);
        }
    }

    public void generatePlayerStorage(UUID uuid) {
        storageMap.put(uuid, new PlayerStorage(
                uuid,
                new HashMap<>()
        ));
    }

    public boolean storageExists(UUID uuid) {
        return storageMap.containsKey(uuid);
    }

    public void updateStorageContents(UUID uuid, Map<Integer, byte[]> contents) {
        storageMap.get(uuid).setContents(contents);
    }

    public Map<Integer, byte[]> getStorageContents(UUID uuid) {
        return storageMap.get(uuid).getContents();
    }
}
