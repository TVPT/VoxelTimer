package com.thevoxelbox.voxeltimer.commands;

import com.thevoxelbox.voxeltimer.VoxelEvent;
import com.thevoxelbox.voxeltimer.VoxelTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandVtStart implements CommandExecutor
{

    private final VoxelTimer plugin;

    public CommandVtStart(VoxelTimer plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        if (!(sender instanceof Player))
        {
            return false;
        }

        Player player = (Player) sender;

        if (player.hasPermission("voxeltimer.commands.start") || player.isOp())
        {
            if (args.length != 2 && args.length != 3)
            {
                return false;
            }
            else
            {
                final ArrayList<VoxelEvent> clearList = new ArrayList<VoxelEvent>();
                final long dateTime = new Date().getTime();
                final List<VoxelEvent> eventList = plugin.getEventList();

                for (VoxelEvent anEventList : eventList)
                {
                    if (anEventList.getTime() < dateTime)
                    {
                        clearList.add(anEventList);
                    }
                }
                eventList.removeAll(clearList);
                boolean startError = false;
                final long DateTime = new Date().getTime();
                final String[] sts = new String[7];
                if (args[0].startsWith("!"))
                {
                    startError = true;
                    player.sendMessage(ChatColor.RED + "May not start eventname with a !");
                }
                for (VoxelEvent anEventList : eventList)
                {
                    if (args[0].equalsIgnoreCase(anEventList.getName()))
                    {
                        startError = true;
                        player.sendMessage(ChatColor.RED + "An event with that name already exists.");
                    }
                }
                sts[0] = args[0];
                final String timetemp = args[1];
                final String[] parsedtime = timetemp.split(":"); // d:h:m
                final int units = parsedtime.length;
                long totalTime = 0;
                switch (units)
                {
                    case 1:
                        totalTime = Math.abs(60000 * Integer.parseInt(parsedtime[0]));
                        break;
                    case 2:
                        totalTime = Math.abs(3600000 * Integer.parseInt(parsedtime[0]) + 60000 * Integer.parseInt(parsedtime[1]));
                        break;
                    case 3:
                        totalTime = Math.abs(86400000 * Integer.parseInt(parsedtime[0]) + 3600000 * Integer.parseInt(parsedtime[1]) + 60000 * Integer.parseInt(parsedtime[2]));
                        break;
                    default:
                        startError = true;
                        player.sendMessage(ChatColor.RED + "Invalid duration");
                }

                sts[2] = Integer.toString(player.getLocation().getBlockX());
                sts[3] = Integer.toString(player.getLocation().getBlockY());
                sts[4] = Integer.toString(player.getLocation().getBlockZ());
                sts[5] = player.getWorld().getName();
                if (args.length == 3 && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")))
                {
                    sts[6] = args[2];
                }
                else
                {
                    sts[6] = "true";
                }

                String eventName = sts[0];
                long time = totalTime + DateTime;
                int x = Integer.parseInt(sts[2]);
                int y = Integer.parseInt(sts[3]);
                int z = Integer.parseInt(sts[4]);
                Vector location = new Vector(x, y, z);
                String world = sts[5];
                boolean teleportOnJoin = Boolean.parseBoolean(sts[6]);
                final VoxelEvent se = new VoxelEvent(eventName, time, location, world, teleportOnJoin);
                if (!startError)
                {
                    eventList.add(se);
                    plugin.saveEvents();
                    player.sendMessage(ChatColor.GREEN + "Event added successfully.");
                }
                return true;
            }
        }
        else
        {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
    }
}