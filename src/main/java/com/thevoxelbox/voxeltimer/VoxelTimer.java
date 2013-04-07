package com.thevoxelbox.voxeltimer;

import com.thevoxelbox.voxeltimer.commands.CommandVtJoin;
import com.thevoxelbox.voxeltimer.commands.CommandVtList;
import com.thevoxelbox.voxeltimer.commands.CommandVtStart;
import com.thevoxelbox.voxeltimer.commands.CommandVtStop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * @author giltwist
 */
public class VoxelTimer extends JavaPlugin {

    private final ArrayList<VoxelEvent> eventList = new ArrayList<VoxelEvent>();

    public ArrayList<VoxelEvent> getEventList() {
        return eventList;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("vtjoin")) {
            if (args.length == 1) {
                List<String> tabCompletes = new ArrayList<String>();
                for (VoxelEvent event : this.eventList) {
                    if (event.name.startsWith(args[0])) {
                        tabCompletes.add(event.name);
                    }
                }
                return tabCompletes;
            }
        }
        return null;
    }

    @Override
    public void onDisable() {
        this.eventList.clear();
    }

    @Override
    public void onEnable() {
        this.getCommand("vtjoin").setExecutor(new CommandVtJoin(this));
        this.getCommand("vtlist").setExecutor(new CommandVtList(this));
        this.getCommand("vtstart").setExecutor(new CommandVtStart(this));
        this.getCommand("vtstop").setExecutor(new CommandVtStop(this));
        this.readEvents();
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
                        this.eventList.add(se);
                    }
                }
            }
            this.getLogger().info("has read " + this.eventList.size() + " events from " + f.getPath());

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

            pw.write("!Eventname,endtime,x,y,z,world,teleportOnJoin\r\n");
            final long DateTime = new Date().getTime();
            for (final VoxelEvent tempwrite : this.eventList) {
                if (tempwrite.endtime > DateTime && !tempwrite.name.startsWith("!")) {
                    pw.write(tempwrite.name + "," + tempwrite.endtime + "," + tempwrite.x + "," + tempwrite.y + "," + tempwrite.z + "," + tempwrite.world
                            + "," + tempwrite.teleportOnJoin + "\r\n");
                }
            }
            pw.close();
        } catch (final Exception e) {
            this.getLogger().warning("[VoxelTimer] Error while writing plugins/VoxelTimer/Events.list");
        }
    }

}
