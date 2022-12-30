package dev.dpvb.survive.chests.hackable;

import dev.dpvb.survive.chests.Loot;
import dev.dpvb.survive.chests.LootSource;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class HackableChestManager implements LootSource {

    private static HackableChestManager instance;
    private final Set<Loot> lootTable = new HashSet<>();
    private final List<HackableChest> hackableChests = new ArrayList<>();
    private final AtomicBoolean clearing = new AtomicBoolean();

    private HackableChestManager() {
        LootSource.loadFromLoot("hackable", lootTable);
    }

    public void spawnHackableChest(Location location) {
        hackableChests.add(new HackableChest(location.getBlock()));
    }

    public void clearHackableChests() {
        clearing.set(true);
        for (HackableChest chest : hackableChests) {
            chest.destroy();
        }
        hackableChests.clear();
        clearing.set(false);
    }

    public void removeHackableChestFromCache(HackableChest hackableChest) {
        hackableChests.remove(hackableChest);
    }

    public boolean isClearing() {
        return clearing.get();
    }

    @Override
    public @NotNull List<ItemStack> generateLoot() {
        return GenerationFormula.DEFAULT.generateAs(lootTable, LinkedList::new);
    }

    public static HackableChestManager getInstance() {
        if (instance == null) {
            instance = new HackableChestManager();
        }

        return instance;
    }
}
