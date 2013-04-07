package com.thevoxelbox.voxeltimer.commands;

import com.thevoxelbox.voxeltimer.VoxelEvent;
import com.thevoxelbox.voxeltimer.VoxelTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandVtList implements CommandExecutor
{

    private static Comparator<VoxelEvent> comparator = new Comparator<VoxelEvent>()
    {
        @Override
        public int compare(final VoxelEvent first, final VoxelEvent second)
        {
            return ((Long) first.getTime()).compareTo(second.getTime());
        }
    };
    private final VoxelTimer plugin;

    public CommandVtList(VoxelTimer plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!sender.hasPermission("voxeltimer.command.list"))
        {
            sender.sendMessage("You do not have sufficient permissions to join events.");
            return true;
        }

        List<VoxelEvent> eventList = plugin.getEventList();

        if (eventList.isEmpty())
        {
            sender.sendMessage(ChatColor.BLUE + "No current Events!");
            return true;
        }

        plugin.validateEvents();

        Collections.sort(eventList, comparator);

        sender.sendMessage(ChatColor.BLUE + "Current Events:");

        final long DateTime = new Date().getTime();
        for (final VoxelEvent event : eventList)
        {
            final long temptime = event.getTime();
            long timeToEvent = temptime - DateTime;
            if (timeToEvent >= 0)
            {
                final long days = TimeUnit.MILLISECONDS.toDays(timeToEvent);
                final long hours = TimeUnit.MILLISECONDS.toHours(timeToEvent) - TimeUnit.DAYS.toHours(days);
                final long minutes = TimeUnit.MILLISECONDS.toMinutes(timeToEvent) - TimeUnit.DAYS.toMinutes(days) - TimeUnit.HOURS.toMinutes(hours);
                final long seconds = TimeUnit.MILLISECONDS.toSeconds(timeToEvent) - TimeUnit.DAYS.toSeconds(days) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);

                String timeRemaining = String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
                sender.sendMessage(ChatColor.GOLD + event.getName() + " - " + timeRemaining);
            }

        }
        return true;
    }
}
