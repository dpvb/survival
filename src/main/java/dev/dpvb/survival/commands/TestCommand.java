package dev.dpvb.survival.commands;

import dev.dpvb.survival.util.skull.SkullGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            player.getInventory().addItem(SkullGenerator.getInstance().getSkullMap().get("right-arrow"));

            player.sendMessage("Skull created.");
        }

        return false;
    }

}
