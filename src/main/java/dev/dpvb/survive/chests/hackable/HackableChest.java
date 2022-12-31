package dev.dpvb.survive.chests.hackable;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.chests.LootSource;
import dev.dpvb.survive.chests.LootableChest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class HackableChest extends LootableChest implements Listener {

    private HackableChestState state;
    private Timer timer;
    private final long hackTime = 30;

    public HackableChest(@NotNull Block block) {
        super(block, Material.ENDER_CHEST, BlockFace.NORTH);
        state = HackableChestState.LOCKED;
        spawnChest();
        Bukkit.getPluginManager().registerEvents(this, Survive.getInstance());
    }

    @Override
    public void destroy() {
        super.destroy();
        HandlerList.unregisterAll(this);
        if (!HackableChestManager.getInstance().isClearing()) {
            HackableChestManager.getInstance().removeHackableChestFromCache(this);
        }
    }

    @EventHandler
    public void onHackableChestOpen(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!block.equals(event.getClickedBlock())) {
            return;
        }

        event.setCancelled(true);
        switch (state) {
            case LOCKED -> {
                // TODO set a time in config.
                timer = new Timer(hackTime);
                timer.runTaskTimer(Survive.getInstance(), 0L, 20L);
                state = HackableChestState.UNLOCKING;
            }
            case UNLOCKED -> open(event.getPlayer());
        }
    }

    @Override
    public @NotNull LootSource getLootSource() {
        return HackableChestManager.getInstance();
    }

    @Override
    protected String hologramText() {
        switch (state) {
            case LOCKED -> {
                return ChatColor.RED + parseTime(hackTime);
            }
            case UNLOCKING -> {
                return ChatColor.YELLOW + parseTime(timer.currTime);
            }
            case UNLOCKED -> {
                return ChatColor.GREEN + "0:00";
            }
        }
        return "BASED";
    }

    private void updateHologram() {
        hologram.getLines().clear();
        hologram.getLines().appendText(hologramText());
    }

    private String parseTime(long seconds) {
        final long m = seconds / 60;
        final long s = seconds % 60;

        return String.format("%02d:%02d", m, s);
    }

    class Timer extends BukkitRunnable {

        long currTime;

        Timer(long seconds) {
            currTime = seconds;
        }

        @Override
        public void run() {
            if (currTime <= 0) {
                cancel();
                state = HackableChestState.UNLOCKED;
            }

            updateHologram();
            currTime--;
        }
    }

}
