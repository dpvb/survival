package dev.dpvb.survival.chests.tiered;

import dev.dpvb.survival.mongo.MongoManager;
import dev.dpvb.survival.mongo.models.ChestData;
import dev.dpvb.survival.mongo.services.ChestDataService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestManager {

    private static ChestManager instance;
    private final Map<Location, LootChest> lootChestMap;

    private ChestManager() {
        lootChestMap = new HashMap<>();
    }

    public static ChestManager getInstance() {
        if (instance == null) {
            instance = new ChestManager();
        }

        return instance;
    }

    public void loadLootChests() {
        final var chestDataList = MongoManager.getInstance().getChestDataService().getAll();
        for (ChestData chestData : chestDataList) {
            final var world = Bukkit.getWorld(chestData.getWorld());
            if (world == null) {
                continue;
            }
            final var loc = new Location(world, chestData.getX(), chestData.getY(), chestData.getZ());
            final var block = world.getBlockAt(loc);
            lootChestMap.put(loc, new LootChest(block, chestData.getTier(), chestData.getFace()));
        }

        Bukkit.getLogger().info("Loaded " + lootChestMap.size() + " loot chests in the arena.");
    }

    public void saveChestsToMongo(Location origin, int radius) {
        int centerX = origin.getBlockX();
        int centerZ = origin.getBlockZ();
        World world = origin.getWorld();
        List<ChestData> chestDataList = new ArrayList<>();
        ChestDataService chestDataService = MongoManager.getInstance().getChestDataService();
        chestDataService.deleteAll();

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                for (int y = world.getMinHeight(); y <= world.getHighestBlockYAt(x, z); y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() != Material.CHEST && block.getType() != Material.ENDER_CHEST) {
                        continue;
                    }

                    ChestData chestData = new ChestData(
                            x,
                            y,
                            z,
                            world.getName(),
                            ((Directional) block.getBlockData()).getFacing(),
                            getTier(block.getType())
                    );

                    chestDataList.add(chestData);
                }
            }
        }

        for (ChestData chestData : chestDataList) {
            chestDataService.create(chestData);
        }
    }

    public Map<Location, LootChest> getLootChestMap() {
        return lootChestMap;
    }

    static ChestTier getTier(Material material) {
        return switch (material) {
            case CHEST -> ChestTier.ONE;
            case ENDER_CHEST -> ChestTier.TWO;
            default -> throw(new IllegalStateException());
        };
    }
}
