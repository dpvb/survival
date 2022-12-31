package dev.dpvb.survive.chests.hackable;

import dev.dpvb.survive.Survive;
import dev.dpvb.survive.chests.LootSource;
import dev.dpvb.survive.chests.LootableChest;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class HackableChest extends LootableChest implements Listener {

    private final long hackTime;
    private HackableChestState state;
    private Timer timer;

    public HackableChest(@NotNull Block block, long hackTime) {
        super(block, Material.ENDER_CHEST, BlockFace.NORTH);
        this.hackTime = hackTime;
        state = HackableChestState.LOCKED;
        spawnChest();
        Bukkit.getPluginManager().registerEvents(this, Survive.getInstance());
    }

    @Override
    public void destroy() {
        super.destroy();
        if (timer != null && !timer.isCancelled()) {
            timer.cancel();
        }
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
        if (hologram.getLines().size() < 1) {
            hologram.getLines().appendText(hologramText());
        } else {
            if (hologram.getLines().get(0) instanceof TextHologramLine textLine) {
                textLine.setText(hologramText());
            } else {
                throw new IllegalStateException("Hologram line is not a text line.");
            }
        }
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
