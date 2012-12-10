package com.thevoxelbox.voxeltimer;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author giltwist
 */
public class VoxelTimer extends JavaPlugin {

    private static TreeSet<String> admns = new TreeSet<String>();
    private static ArrayList<VoxelEvent> EventList = new ArrayList<VoxelEvent>();
    private static Comparator<VoxelEvent> chrono = new Comparator<VoxelEvent>() {
        @Override
        public int compare(final VoxelEvent first, final VoxelEvent second) {
            return ((Long) first.endtime).compareTo(second.endtime);
        }
    };
    private File admnsFile;

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player _player = (Player) sender;

        if (command.getName().equalsIgnoreCase("vtstart")) {
            if (VoxelTimer.admns.contains(_player.getName())) {
                return this.commandVtStart(args, _player);
            } else {
                _player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        } else if (command.getName().equalsIgnoreCase("vtstop")) {
            if (VoxelTimer.admns.contains(_player.getName())) {
                return this.commandVtStop(args, _player);
            } else {
                _player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        } else if (command.getName().equalsIgnoreCase("vtlist")) {
            return this.commandVtList(_player);
        } else if (command.getName().equalsIgnoreCase("vtjoin")) {
            return this.commandVtJoin(args, _player);
        } else {
            return false;
        }
        return false;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(command.getName().equalsIgnoreCase("vtjoin")) {
        	if(args.length == 1) {
	        	List<String> tabCompletes = new ArrayList<String>();
	            for(VoxelEvent event : VoxelTimer.EventList) {
	            	if(event.eventname.startsWith(args[0])) {
	            		tabCompletes.add(event.eventname);
	            	}
	            }
	            return tabCompletes;
        	}
        }
        return null;
    }
    @Override
    public void onDisable() {
        VoxelTimer.EventList.clear();
    }

    @Override
    public void onEnable() {
        this.readAdmins();
        this.readEvents();
        this.getLogger().info("ticking.");
    }

    public void readAdmins() {
        try {
            this.admnsFile = new File("plugins/admns.txt");
            if (!this.admnsFile.exists()) {
                this.admnsFile.createNewFile();
                this.getLogger().info("admns.txt was missing and thus created.");
            }
            final Scanner snr = new Scanner(this.admnsFile);
            while (snr.hasNext()) {
                final String st = snr.nextLine();
                VoxelTimer.admns.add(st);
            }
            snr.close();
        } catch (final Exception e) {
            this.getLogger().warning("Error while loading admns.txt");
        }
    }

    public void readEvents() {
        try {
            final File f = new File("plugins/VoxelTimer/Events.list");
            if (!f.exists()) {
                this.getLogger().info(f.getPath() + " not found; Creating one.");
                this.writeEvents();
            }
            final Scanner snr = new Scanner(f);
            final long DateTime = new Date().getTime();
            while (snr.hasNext()) {
                final String st = snr.nextLine();
                if (!st.startsWith("!")) {
                    final String[] sts = st.split(",");
                    final VoxelEvent se = new VoxelEvent(sts[0], Long.parseLong(sts[1]), Integer.parseInt(sts[2]), Integer.parseInt(sts[3]),
                            Integer.parseInt(sts[4]), sts[5], Boolean.parseBoolean(sts[6]));
                    if (se.endtime > DateTime) {
                        VoxelTimer.EventList.add(se);
                    }
                }
            }
            this.getLogger().info("has read " + VoxelTimer.EventList.size() + " events from " + f.getPath());

            snr.close();
        } catch (final Exception e) {
            this.getLogger().warning("Error while loading events.");
        }
    }

    public void writeEvents() {
        try {
            final File oldFile = new File("plugins/VoxelTimer/Events.list");
            if (!oldFile.getParentFile().isDirectory()) {
                oldFile.mkdirs();
            }
            oldFile.delete();

            final File newFile = new File("plugins/VoxelTimer/Events.list");
            newFile.createNewFile();

            final PrintWriter pw = new PrintWriter(new File("plugins/VoxelTimer/Events.list"));

            pw.write("!Eventname,endtime,x,y,z,world,tponjoin\r\n");
            final long DateTime = new Date().getTime();
            for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
                final VoxelEvent tempwrite = VoxelTimer.EventList.get(i);
                if (tempwrite.endtime > DateTime && !tempwrite.eventname.startsWith("!")) {
                    pw.write(tempwrite.eventname + "," + tempwrite.endtime + "," + tempwrite.x + "," + tempwrite.y + "," + tempwrite.z + "," + tempwrite.world
                            + "," + tempwrite.tponjoin + "\r\n");
                }
            }
            pw.close();
        } catch (final Exception e) {
            this.getLogger().warning("[VoxelTimer] Error while writing plugins/VoxelTimer/Events.list");
        }
    }

    /**
     * @param args
     * @param player
     * @return boolean
     */
    private boolean commandVtJoin(final String[] args, final Player player) {
        if (args.length != 1) {
            return false;
        } else {
            boolean jointemp = true;
            final long DateTime = new Date().getTime();
            for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
                if (args[0].equalsIgnoreCase(VoxelTimer.EventList.get(i).eventname)) {
                    if (VoxelTimer.EventList.get(i).endtime > DateTime) {
                        jointemp = false;
                        if (VoxelTimer.EventList.get(i).tponjoin) {
                            player.teleport(new Location(player.getServer().getWorld(VoxelTimer.EventList.get(i).world), VoxelTimer.EventList.get(i).x,
                                    VoxelTimer.EventList.get(i).y, VoxelTimer.EventList.get(i).z));
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

    /**
     * @param player
     * @return boolean
     */
    private boolean commandVtList(final Player player) {
        Collections.sort(VoxelTimer.EventList, VoxelTimer.chrono);

        if (VoxelTimer.EventList.isEmpty()) {
            player.sendMessage(ChatColor.BLUE + "No current Events!");
            return true;
        }

        player.sendMessage(ChatColor.BLUE + "Current Events:");
        for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
            final VoxelEvent listtemp = VoxelTimer.EventList.get(i);
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

                player.sendMessage(ChatColor.GOLD + listtemp.eventname + " - " + tempremaining);
            }

        }
        return true;
    }

    /**
     * @param args
     * @param player
     * @return boolean
     */
    private boolean commandVtStart(final String[] args, final Player player) {
        if (args.length != 2 && args.length != 3) {
            return false;
        } else {
            final ArrayList<VoxelEvent> ClearList = new ArrayList<VoxelEvent>();
            final long DateTime1 = new Date().getTime();
            for (int i1 = 0; i1 < VoxelTimer.EventList.size(); i1++) {
                if (VoxelTimer.EventList.get(i1).endtime < DateTime1) {
                    ClearList.add(VoxelTimer.EventList.get(i1));
                }
            }
            VoxelTimer.EventList.removeAll(ClearList);
            boolean starterror = false;
            final long DateTime = new Date().getTime();
            final String[] sts = new String[7];
            if (args[0].startsWith("!")) {
                starterror = true;
                player.sendMessage(ChatColor.RED + "May not start eventname with a !");
            }
            for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
                if (args[0].equalsIgnoreCase(VoxelTimer.EventList.get(i).eventname)) {
                    starterror = true;
                    player.sendMessage(ChatColor.RED + "An event with that name already exists.");
                }
            }
            sts[0] = args[0];
            final String timetemp = args[1];
            final String[] parsedtime = timetemp.split(":"); // d:h:m
            final int units = parsedtime.length;
            long totaltime = 0;
            switch (units) {
            case 1:
                totaltime = Math.abs(60000 * Integer.parseInt(parsedtime[0]));
                break;
            case 2:
                totaltime = Math.abs(3600000 * Integer.parseInt(parsedtime[0]) + 60000 * Integer.parseInt(parsedtime[1]));
                break;
            case 3:
                totaltime = Math.abs(86400000 * Integer.parseInt(parsedtime[0]) + 3600000 * Integer.parseInt(parsedtime[1]) + 60000
                        * Integer.parseInt(parsedtime[2]));
                break;
            default:
                starterror = true;
                player.sendMessage(ChatColor.RED + "Invalid duration");
            }

            sts[2] = Integer.toString(player.getLocation().getBlockX());
            sts[3] = Integer.toString(player.getLocation().getBlockY());
            sts[4] = Integer.toString(player.getLocation().getBlockZ());
            sts[5] = player.getWorld().getName();
            if (args.length == 3 && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {
                sts[6] = args[2];
            } else {
                sts[6] = "true";
            }

            final VoxelEvent se = new VoxelEvent(sts[0], totaltime + DateTime, Integer.parseInt(sts[2]), Integer.parseInt(sts[3]), Integer.parseInt(sts[4]),
                    sts[5], Boolean.parseBoolean(sts[6]));
            if (!starterror) {
                VoxelTimer.EventList.add(se);
                this.writeEvents();
                player.sendMessage(ChatColor.GREEN + "Event added successfully.");
            }
            return true;
        }
    }

    /**
     * @param args
     * @param player
     * @return boolean
     */
    private boolean commandVtStop(final String[] args, final Player player) {
        if (args.length != 1) {
            return false;
        } else {
            int stoptemp = -1;
            for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
                if (args[0].equalsIgnoreCase(VoxelTimer.EventList.get(i).eventname)) {
                    stoptemp = i;
                }
            }

            if (stoptemp >= 0) {
                VoxelTimer.EventList.remove(stoptemp);
                this.writeEvents();
                player.sendMessage(ChatColor.GOLD + "Event deleted.");
            } else {
                player.sendMessage(ChatColor.RED + "Event not found.");
            }
            return true;
        }
    }
}
