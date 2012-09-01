/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Code;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author giltwist
 */
public class VoxelTimer extends JavaPlugin {

    private Player p;
    private World w;
    protected static final Logger log = Logger.getLogger("Minecraft");
    public static TreeSet<String> admns = new TreeSet<String>();
    public static ArrayList<VoxelEvent> EventList = new ArrayList<VoxelEvent>();

    @Override
    public void onDisable() {
        VoxelTimer.EventList.clear();
    }

    @Override
    public void onEnable() {
        readAdmins();
        readEvents();
        log.info("[VoxelTimer] ticking.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        p = (Player) sender;
        w = p.getWorld();
        boolean commstatus = true;



        String comm = command.getName().toLowerCase();

        if (comm.equals("vtstart")) {

            if (admns.contains(p.getName())) {

                if (args.length != 2 && args.length != 3) {
                    p.sendMessage("Use: /vtstart <eventname> <duration> [tponjoin:default true]");
                } else {
                    dovtstart(args);
                }
            } else {

                p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }

        } else if (comm.equals("vtstop")) {

            if (admns.contains(p.getName())) {

                if (args.length != 1) {
                    p.sendMessage("Use: /vtstop <eventname>");
                } else {
                    dovtstop(args);
                }
            } else {

                p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }

        } else if (comm.equals("vtlist")) {
            dovtlist();
        } else if (comm.equals("vtjoin")) {
            if (args.length != 1) {
                p.sendMessage("Use: /vtjoin <eventname>");
            } else {
                dovtjoin(args);
            }
        } else {
            commstatus = false;
        }


        return commstatus;

    }

    public void dovtstart(String[] vtargs) {
        clearEvents();
        boolean starterror = false;
        long DateTime = new Date().getTime();
        String[] sts = new String[7];
        if (vtargs[0].startsWith("!")) {
            starterror = true;
            p.sendMessage(ChatColor.RED + "May not start eventname with a !");
        }
        for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
            if (vtargs[0].equalsIgnoreCase(VoxelTimer.EventList.get(i).eventname)) {
                starterror = true;
                p.sendMessage(ChatColor.RED + "An event with that name already exists.");
            }
        }
        sts[0] = vtargs[0];
        String timetemp = vtargs[1];
        String[] parsedtime = timetemp.split(":"); //d:h:m
        int units = parsedtime.length;
        //long[] unitfactor={86400000,3600000,60000};
        long totaltime = 0;
        switch (units) {
            case 1:
                totaltime = Math.abs(60000 * Integer.parseInt(parsedtime[0]));
                break;
            case 2:
                totaltime = Math.abs(3600000 * Integer.parseInt(parsedtime[0]) + 60000 * Integer.parseInt(parsedtime[1]));
                break;
            case 3:
                totaltime = Math.abs(86400000 * Integer.parseInt(parsedtime[0]) + 3600000 * Integer.parseInt(parsedtime[1]) + 60000 * Integer.parseInt(parsedtime[2]));
                break;
            default:
                starterror = true;
                p.sendMessage(ChatColor.RED + "Invalid duration");
        }

        sts[2] = Integer.toString(p.getLocation().getBlockX());
        sts[3] = Integer.toString(p.getLocation().getBlockY());
        sts[4] = Integer.toString(p.getLocation().getBlockZ());
        sts[5] = p.getWorld().getName();
        if (vtargs.length == 3 && (vtargs[2].equalsIgnoreCase("true") || vtargs[2].equalsIgnoreCase("false"))) {
            sts[6] = vtargs[2];
        } else {
            sts[6] = "true";
        }

        VoxelEvent se = new VoxelEvent(sts[0], totaltime + DateTime, Integer.parseInt(sts[2]), Integer.parseInt(sts[3]), Integer.parseInt(sts[4]), sts[5], Boolean.parseBoolean(sts[6]));
        if (!starterror) {
            VoxelTimer.EventList.add(se);
            writeEvents();
            p.sendMessage(ChatColor.GREEN + "Event added successfully.");
        }
    }

    public void dovtstop(String[] vtargs) {
        int stoptemp = -1;
        for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
            if (vtargs[0].equalsIgnoreCase(VoxelTimer.EventList.get(i).eventname)) {
                stoptemp = i;
            }
        }

        if (stoptemp >= 0) {
            VoxelTimer.EventList.remove(stoptemp);
            writeEvents();
            p.sendMessage(ChatColor.GOLD + "Event deleted.");
        } else {
            p.sendMessage(ChatColor.RED + "Event not found.");
        }

    }

    public void dovtlist() {
        p.sendMessage(ChatColor.BLUE + "Current Events:");
        ArrayList<VoxelEvent> SortList = VoxelTimer.EventList;
               
        Comparator chrono=new Comparator<VoxelEvent>() {

            public int compare(VoxelEvent first, VoxelEvent second) {
                Long firsttime=first.endtime;
                Long secondtime=second.endtime;
                return firsttime.compareTo(secondtime);
            }
        };
        Collections.sort(SortList, chrono);
        
        for (int i = 0; i < SortList.size(); i++) {
            VoxelEvent listtemp = SortList.get(i);
            long DateTime = new Date().getTime();
            long temptime = listtemp.endtime;
            long timeoffset = temptime - DateTime;
            if (timeoffset > 0) {
                int tempday = (int) Math.max(Math.floor(timeoffset / 86400000), 0);
                timeoffset = timeoffset - tempday * 86400000;
                int temphour = (int) Math.max(Math.floor(timeoffset / 3600000), 0);
                timeoffset = timeoffset - temphour * 3600000;
                int tempminute = (int) Math.max(Math.floor(timeoffset / 60000), 0);
                timeoffset = timeoffset - tempminute * 60000;
                int tempsec = (int) Math.max(Math.floor(timeoffset / 1000), 0);
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


                p.sendMessage(ChatColor.GOLD + listtemp.eventname + " - " + tempremaining);
            }

        }

    }

    public void dovtjoin(String[] vtargs) {
        boolean jointemp = true;
        long DateTime = new Date().getTime();
        for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
        	if (VoxelTimer.EventList.get(i).eventname.startsWith(vtargs[0])) {
                if (VoxelTimer.EventList.get(i).endtime > DateTime) {
                    jointemp = false;
                    if (VoxelTimer.EventList.get(i).tponjoin) {
                        p.teleport(new Location(p.getServer().getWorld(VoxelTimer.EventList.get(i).world), (double) VoxelTimer.EventList.get(i).x, (double) VoxelTimer.EventList.get(i).y, (double) VoxelTimer.EventList.get(i).z));
                    } else {
                        p.sendMessage(ChatColor.RED + "There is no teleport associated with that event.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "That event has ended.");
                }
            }
        }
        if (jointemp) {
            p.sendMessage(ChatColor.RED + "Event not found.");
        }
    }

    public void readAdmins() { //Shamelessly stolen from VoxelGuest to maintain inter-plugin compatibility
        try {
            File f = new File("plugins/admns.txt");
            if (!f.exists()) {
                f.createNewFile();
                log.info("[VoxelTimer] admns.txt was missing and thus created.");
            }
            Scanner snr = new Scanner(f);
            while (snr.hasNext()) {
                String st = snr.nextLine();
                admns.add(st);
            }
            snr.close();
        } catch (Exception e) {
            log.warning("[VoxelTimer] Error while loading admns.txt");
        }
    }

    public void readEvents() {
        try {
            File f = new File("plugins/VoxelTimer/Events.list");
            if (!f.exists()) {
                log.info("[VoxelTimer] plugins/VoxelTimer/Events.list not found; Creating one.");
                writeEvents();
            }
            Scanner snr = new Scanner(f);
            long DateTime = new Date().getTime();
            while (snr.hasNext()) {
                String st = snr.nextLine();
                if (!st.startsWith("!")) {
                    String[] sts = st.split(",");
                    VoxelEvent se = new VoxelEvent(sts[0], Long.parseLong(sts[1]), Integer.parseInt(sts[2]), Integer.parseInt(sts[3]), Integer.parseInt(sts[4]), sts[5], Boolean.parseBoolean(sts[6]));
                    if (se.endtime > DateTime) {
                        VoxelTimer.EventList.add(se);
                    }
                }
            }
            log.info("[VoxelTimer] has read " + VoxelTimer.EventList.size() + " events from plugins/VoxelTimer/Events.list");

            snr.close();
        } catch (Exception e) {
            log.warning("[VoxelTimer] Error while loading plugins/VoxelTimer/Events.list");
        }
    }

    public void writeEvents() {

        try {
            File oldFile = new File("plugins/VoxelTimer/Events.list");
            if (!oldFile.getParentFile().isDirectory()) {
                oldFile.mkdirs();
            }
            oldFile.delete();

            File newFile = new File("plugins/VoxelTimer/Events.list");
            newFile.createNewFile();

            PrintWriter pw = new PrintWriter(new File("plugins/VoxelTimer/Events.list"));

            pw.write("!Eventname,endtime,x,y,z,world,tponjoin\r\n");
            long DateTime = new Date().getTime();
            for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
                VoxelEvent tempwrite = VoxelTimer.EventList.get(i);
                if (tempwrite.endtime > DateTime && !tempwrite.eventname.startsWith("!")) {
                    pw.write(tempwrite.eventname + "," + tempwrite.endtime + "," + tempwrite.x + "," + tempwrite.y + "," + tempwrite.z + "," + tempwrite.world + "," + tempwrite.tponjoin + "\r\n");
                }
            }
            pw.close();
        } catch (Exception e) {
            log.warning("[VoxelTimer] Error while writing plugins/VoxelTimer/Events.list");
        }
    }

    public void clearEvents() {
        ArrayList<VoxelEvent> ClearList = new ArrayList<VoxelEvent>();
        long DateTime = new Date().getTime();
        for (int i = 0; i < VoxelTimer.EventList.size(); i++) {
            if (VoxelTimer.EventList.get(i).endtime < DateTime) {
                ClearList.add(VoxelTimer.EventList.get(i));
            }
        }
        VoxelTimer.EventList.removeAll(ClearList);
    }
}
