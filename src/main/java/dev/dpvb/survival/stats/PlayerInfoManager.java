package dev.dpvb.survival.stats;

import dev.dpvb.survival.mongo.MongoManager;
import dev.dpvb.survival.mongo.models.PlayerInfo;
import dev.dpvb.survival.mongo.services.PlayerInfoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerInfoManager {

    private static PlayerInfoManager instance;

    private Map<UUID, PlayerInfo> map;

    private PlayerInfoManager() {
        map = new HashMap<>();
    }

    public static PlayerInfoManager getInstance() {
        if (instance == null) {
            instance = new PlayerInfoManager();
        }
        return instance;
    }

    public void load() {
        List<PlayerInfo> all = MongoManager.getInstance().getPlayerInfoService().getAll();
        for (PlayerInfo playerInfo : all) {
            map.put(playerInfo.getId(), playerInfo);
        }
    }

    public void save() {
        final PlayerInfoService playerInfoService = MongoManager.getInstance().getPlayerInfoService();
        for (Map.Entry<UUID, PlayerInfo> entry : map.entrySet()) {
            playerInfoService.replace(entry.getValue());
        }
    }

    public void generatePlayerInfo(UUID uuid) {
        map.put(uuid, new PlayerInfo(
                uuid,
                0
        ));
    }

    public boolean playerInfoExists(UUID uuid) {
        return map.containsKey(uuid);
    }

    public void addTokens(UUID uuid, int tokens) {
        PlayerInfo info = map.get(uuid);
        info.setTokens(info.getTokens() + tokens);
    }

    public void setTokens(UUID uuid, int tokens) {
        map.get(uuid).setTokens(tokens);
    }

    public int getTokens(UUID uuid) {
        return map.get(uuid).getTokens();
    }
}