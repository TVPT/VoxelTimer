package com.thevoxelbox.voxeltimer.commands;

import com.thevoxelbox.voxeltimer.VoxelEvent;
import com.thevoxelbox.voxeltimer.VoxelTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CommandVtList implements CommandExecutor {

    private static Comparator<VoxelEvent> comparator = new Comparator<VoxelEvent>() {
        @Override
        public int compare(final VoxelEvent first, final VoxelEvent second) {
            return ((Long) first.endtime).compareTo(second.endtime);
        }
    };
    private final VoxelTimer plugin;

    public CommandVtList(VoxelTimer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;


        ArrayList<VoxelEvent> eventList = plugin.getEventList();
        Collections.sort(eventList, comparator);

        if (eventList.isEmpty()) {
            player.sendMessage(ChatColor.BLUE + "No current Events!");
            return true;
        }

        player.sendMessage(ChatColor.BLUE + "Current Events:");
        for (int i = 0; i < eventList.size(); i++) {
            final VoxelEvent listtemp = eventList.get(i);
            final long DateTime = new Date().getTime();
            final long temptime = listtemp.endtime;
            long timeoffset = temptime - DateTime;
            if (timeoffset > 0) {
                final int tempday = (int) Math.max(Math.floor(timeoffset / 86400000), 0);
                timeoffset = timeoffset - tempday * 86400000;
                final int temphour = (int) Math.max(Math.floor(timeoffset / 3600000), 0);
                timeoffset = timeoffset - temphour * 3600000;
                final int tempminute = (int) Math.max(Math.floor(timeoffset / 60000), 0);
                timeoffset = timeoffset - tempminute * 60000;
                final int tempsec = (int) Math.max(Math.floor(timeoffset / 1000), 0);
                timeoffset = timeoffset - tempsec * 1000;

                String tempremaining = "";
                boolean tempfirst = false;

                if (tempday > 0) {
                    tempfirst = true;
                    tempremaining = tempremaining.concat(Integer.toString(tempday) + "d ");
                }
                if (temphour > 0 || tempfirst) {
                    tempfirst = true;
                    tempremaining = tempremaining.concat(Integer.toString(temphour) + "h ");
                }
                if (tempminute > 0 || tempfirst) {
                    tempfirst = true;
                    tempremaining = tempremaining.concat(Integer.toString(tempminute) + "m ");
                }

                tempremaining = tempremaining.concat(Integer.toString(tempsec) + "s");

                player.sendMessage(ChatColor.GOLD + listtemp.name + " - " + tempremaining);
            }

        }
        return true;
    }
}
