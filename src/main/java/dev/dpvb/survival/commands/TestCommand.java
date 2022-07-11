package dev.dpvb.survival.commands;

import dev.dpvb.survival.npc.BasicEnchanterNPC;
import dev.dpvb.survival.npc.NPCManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            NPCManager.getInstance().addNPC(new BasicEnchanterNPC(player.getLocation()));

            player.sendMessage("NPC Created.");
        }

        return false;
    }
}
