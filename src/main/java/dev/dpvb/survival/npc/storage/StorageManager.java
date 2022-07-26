package dev.dpvb.survival.npc.storage;

import dev.dpvb.survival.mongo.MongoManager;
import dev.dpvb.survival.mongo.models.PlayerStorage;
import dev.dpvb.survival.mongo.services.PlayerStorageService;

import java.util.*;

public class StorageManager {

    private static StorageManager instance;

    private final Map<UUID, Map<Integer, byte[]>> storageMap;

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
            Map<String, byte[]> fromContents = storage.getContents();
            Map<Integer, byte[]> toContents = new HashMap<>();

            fromContents.forEach((key, value) -> toContents.put(Integer.parseInt(key), value));

            storageMap.put(storage.getId(), toContents);
        }
    }

    public void save() {
        final PlayerStorageService pss = MongoManager.getInstance().getPlayerStorageService();
        for (Map.Entry<UUID, Map<Integer, byte[]>> entry : storageMap.entrySet()) {
            Map<Integer, byte[]> fromContents = entry.getValue();
            Map<String, byte[]> toContents = new HashMap<>();

            for (Map.Entry<Integer, byte[]> itemEntry : fromContents.entrySet()) {
                toContents.put(String.valueOf(itemEntry.getKey()), itemEntry.getValue());
            }

            pss.replace(new PlayerStorage(
                    entry.getKey(),
                    toContents
            ));
        }
    }

    public void generatePlayerStorage(UUID uuid) {
        storageMap.put(uuid, new HashMap<>());
    }

    public boolean storageExists(UUID uuid) {
        return storageMap.containsKey(uuid);
    }

    public void updateStorageContents(UUID uuid, Map<Integer, byte[]> contents) {
        storageMap.put(uuid, contents);
    }

    public Map<Integer, byte[]> getStorageContents(UUID uuid) {
        return storageMap.get(uuid);
    }
}
