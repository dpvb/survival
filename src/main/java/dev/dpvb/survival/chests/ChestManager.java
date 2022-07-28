package dev.dpvb.survival.chests;

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
import java.util.List;

public class ChestManager {

    private static ChestManager instance;
    private ChestDataService chestDataService;

    private ChestManager() {
        chestDataService = MongoManager.getInstance().getChestDataService();
    }

    public static ChestManager getInstance() {
        if (instance == null) {
            instance = new ChestManager();
        }

        return instance;
    }

    public void saveChestsToMongo(Location origin, int radius) {
        int centerX = origin.getBlockX();
        int centerZ = origin.getBlockZ();
        World world = origin.getWorld();
        List<ChestData> chestDataList = new ArrayList<>();
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
                            ChestTier.getTier(block.getType())
                    );

                    chestDataList.add(chestData);
                }
            }
        }

        for (ChestData chestData : chestDataList) {
            chestDataService.create(chestData);
        }
    }
}
