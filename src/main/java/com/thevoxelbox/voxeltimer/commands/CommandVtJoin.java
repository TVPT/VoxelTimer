package com.thevoxelbox.voxeltimer.commands;

import com.thevoxelbox.voxeltimer.VoxelEvent;
import com.thevoxelbox.voxeltimer.VoxelTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandVtJoin implements CommandExecutor, TabCompleter
{

    private final VoxelTimer plugin;

    public CommandVtJoin(VoxelTimer plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("Only Players can execute this command.");
            return true;
        }

        if (!sender.hasPermission("voxeltimer.command.join"))
        {
            sender.sendMessage("You do not have sufficient permissions to join events.");
            return true;
        }

        if (args.length != 1)
        {
            return false;
        }

        Player player = (Player) sender;

        plugin.validateEvents();

        final long currentTime = new Date().getTime();

        for (VoxelEvent event : plugin.getEventList())
        {
            if (event.getName().equalsIgnoreCase(args[0]) && event.getTime() > currentTime)
            {
                if (event.isTeleportOnJoin())
                {
                    World world = Bukkit.getWorld(event.getWorld());
                    if (world == null)
                    {
                        player.sendMessage(ChatColor.RED + "ERROR: World was not found on server.");
                        return true;
                    }
                    Location location = event.getLocation().toLocation(world);
                    player.teleport(location);
                    return true;
                }
                else
                {
                    player.sendMessage(ChatColor.RED + "There is no teleport associated with that event.");
                    return true;
                }
            }
        }

        player.sendMessage(ChatColor.RED + "Event not found.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            List<String> tabCompletes = new ArrayList<String>();
            for (VoxelEvent event : plugin.getEventList())
            {
                if (event.getName().startsWith(args[0]))
                {
                    tabCompletes.add(event.getName());
                }
            }
            return tabCompletes;
        }
        return null;
    }
}
