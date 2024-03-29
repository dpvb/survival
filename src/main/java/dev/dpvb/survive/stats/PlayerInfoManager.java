package dev.dpvb.survive.stats;

import dev.dpvb.survive.mongo.MongoManager;
import dev.dpvb.survive.mongo.models.PlayerInfo;
import dev.dpvb.survive.mongo.services.PlayerInfoService;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerInfoManager {

    private static PlayerInfoManager instance;

    private final Map<UUID, PlayerInfo> map;

    private PlayerInfoManager() {
        map = new HashMap<>();
    }

    public static PlayerInfoManager getInstance() {
        if (instance == null) {
            instance = new PlayerInfoManager();
        }
        return instance;
    }

    /**
     * Loads the Player Info Statistics from the Mongo DB.
     */
    public void load() {
        List<PlayerInfo> all = MongoManager.getInstance().getPlayerInfoService().getAll();
        for (PlayerInfo playerInfo : all) {
            map.put(playerInfo.getId(), playerInfo);
        }
    }

    /**
     * Saves all the current information in the {@link PlayerInfoManager#map} to the MongoDB
     */
    public void save() {
        final PlayerInfoService playerInfoService = MongoManager.getInstance().getPlayerInfoService();
        for (PlayerInfo playerInfo : map.values()) {
            playerInfoService.replace(playerInfo);
        }
    }

    /**
     * Generates a new {@link PlayerInfo} instance and puts it in the {@link PlayerInfoManager#map}
     * @param uuid The UUID of the Player.
     */
    public void generatePlayerInfo(UUID uuid) {
        map.put(uuid, new PlayerInfo(
                uuid,
                0,
                0,
                0
        ));
    }

    /**
     * Checks if a certain player's information already exists.
     * @param uuid The UUID of the Player
     * @return True if the information exists.
     */
    public boolean playerInfoExists(UUID uuid) {
        return map.containsKey(uuid);
    }

    /**
     * Add a number of tokens to the player.
     * <p>
     * Changes the token amount by the amount specified by <code>tokens</code>
     * (positive or negative).
     *
     * @param uuid The UUID of the Player
     * @param tokens The amount of tokens to add.
     */
    public void addTokens(UUID uuid, int tokens) {
        PlayerInfo info = map.get(uuid);
        info.setTokens(info.getTokens() + tokens);
    }

    public void addKill(UUID uuid) {
        PlayerInfo info = map.get(uuid);
        info.setKills(info.getKills() + 1);
    }

    public void addDeath(UUID uuid) {
        PlayerInfo info = map.get(uuid);
        info.setDeaths(info.getDeaths() + 1);
    }

    /**
     * Set the Player's tokens to a specific amount.
     * @param uuid The UUID of the Player
     * @param tokens The amount of tokens to set to.
     */
    public void setTokens(UUID uuid, int tokens) {
        map.get(uuid).setTokens(tokens);
    }

    /**
     * Gets the Player's current amount of tokens.
     * @param uuid The UUID of the Player
     * @return the number of tokens the Player has.
     */
    public int getTokens(UUID uuid) {
        return map.get(uuid).getTokens();
    }

    public List<PlayerInfo> getTopKills(int amount) {
        amount = Math.min(amount, map.size());
        return map.values().stream()
                .sorted((p1, p2) -> p2.getKills().compareTo(p1.getKills()))
                .limit(amount)
                .collect(Collectors.toList());
    }
}