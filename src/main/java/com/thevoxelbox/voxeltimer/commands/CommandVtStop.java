package com.thevoxelbox.voxeltimer.commands;

import com.thevoxelbox.voxeltimer.VoxelEvent;
import com.thevoxelbox.voxeltimer.VoxelTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandVtStop implements CommandExecutor {
    private final VoxelTimer plugin;

    public CommandVtStop(VoxelTimer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (player.hasPermission("voxeltimer.commands.stop") || player.isOp()) {
            if (args.length != 1) {
                return false;
            } else {
                int stoptemp = -1;
                final List<VoxelEvent> eventList = plugin.getEventList();
                for (int i = 0; i < eventList.size(); i++) {
                    if (args[0].equalsIgnoreCase(eventList.get(i).getName())) {
                        stoptemp = i;
                    }
                }

                if (stoptemp >= 0) {
                    eventList.remove(stoptemp);
                    plugin.saveEvents();
                    player.sendMessage(ChatColor.GOLD + "Event deleted.");
                } else {
                    player.sendMessage(ChatColor.RED + "Event not found.");
                }
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
    }
}
