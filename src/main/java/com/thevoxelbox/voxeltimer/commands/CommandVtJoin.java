package com.thevoxelbox.voxeltimer.commands;

import com.thevoxelbox.voxeltimer.VoxelEvent;
import com.thevoxelbox.voxeltimer.VoxelTimer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;

public class CommandVtJoin implements CommandExecutor {

    private final VoxelTimer plugin;

    public CommandVtJoin(VoxelTimer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            return false;
        } else {
            boolean jointemp = true;
            final long DateTime = new Date().getTime();
            ArrayList<VoxelEvent> eventList = plugin.getEventList();
            for (int i = 0; i < eventList.size(); i++) {
                if (args[0].equalsIgnoreCase(eventList.get(i).name)) {
                    if (eventList.get(i).endtime > DateTime) {
                        jointemp = false;
                        if (eventList.get(i).teleportOnJoin) {
                            player.teleport(new Location(player.getServer().getWorld(eventList.get(i).world), eventList.get(i).x,
                                    eventList.get(i).y, eventList.get(i).z));
                        } else {
                            player.sendMessage(ChatColor.RED + "There is no teleport associated with that event.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "That event has ended.");
                    }
                }
            }
            if (jointemp) {
                player.sendMessage(ChatColor.RED + "Event not found.");
            }
            return true;
        }
    }
}
