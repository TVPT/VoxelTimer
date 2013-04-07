package com.thevoxelbox.voxeltimer;

import com.thevoxelbox.voxeltimer.commands.CommandVtJoin;
import com.thevoxelbox.voxeltimer.commands.CommandVtList;
import com.thevoxelbox.voxeltimer.commands.CommandVtStart;
import com.thevoxelbox.voxeltimer.commands.CommandVtStop;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

/**
 * @author giltwist
 */
public class VoxelTimer extends JavaPlugin
{

    private final List<VoxelEvent> eventList = new ArrayList<VoxelEvent>();

    public List<VoxelEvent> getEventList()
    {
        return eventList;
    }

    @Override
    public void onDisable()
    {
        saveEvents();
        this.eventList.clear();
    }

    @Override
    public void onEnable()
    {
        ConfigurationSerialization.registerClass(VoxelEvent.class);

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.migrateEvents();

        List savedEvents = this.getConfig().getList("events", null);
        if (savedEvents == null)
            savedEvents = new ArrayList<VoxelEvent>();
        eventList.clear();
        eventList.addAll(savedEvents);
        this.validateEvents();

        this.getLogger().info(eventList.size() + " events loaded.");

        this.getCommand("vtjoin").setExecutor(new CommandVtJoin(this));
        this.getCommand("vtlist").setExecutor(new CommandVtList(this));
        this.getCommand("vtstart").setExecutor(new CommandVtStart(this));
        this.getCommand("vtstop").setExecutor(new CommandVtStop(this));
    }

    public void migrateEvents()
    {
        try
        {
            final File file = new File(this.getDataFolder(), "Events.list");
            if (!file.exists())
            {
                return;
            }
            final Scanner scanner = new Scanner(file);
            final long DateTime = new Date().getTime();
            List savedEvents = this.getConfig().getList("events");
            if (savedEvents == null)
                savedEvents = new ArrayList<VoxelEvent>();
            eventList.clear();
            eventList.addAll(savedEvents);

            int migrationCounter = 0;
            while (scanner.hasNext())
            {
                final String line = scanner.nextLine();
                if (line.startsWith("!"))
                {
                    continue;
                }

                final String[] splittedData = line.split(",");
                String name = splittedData[0];
                long time = Long.parseLong(splittedData[1]);
                int x = Integer.parseInt(splittedData[2]);
                int y = Integer.parseInt(splittedData[3]);
                int z = Integer.parseInt(splittedData[4]);
                Vector location = new Vector(x, y, z);
                String world = splittedData[5];
                boolean teleportOnJoin = Boolean.parseBoolean(splittedData[6]);

                final VoxelEvent event = new VoxelEvent(name, time, location, world, teleportOnJoin);

                if (event.getTime() > DateTime)
                {
                    this.eventList.add(event);
                    migrationCounter++;
                }

            }
            scanner.close();
            file.delete();

            saveEvents();
            this.reloadConfig();

            this.getLogger().info("migrated " + migrationCounter + " events.");
        }
        catch (final Exception e)
        {
            this.getLogger().warning("Error while migrating events.");
        }
    }

    public void saveEvents()
    {
        this.getConfig().set("events", eventList);
        this.saveConfig();
    }

    public void validateEvents()
    {
        final long currentTime = new Date().getTime();
        for (Iterator<VoxelEvent> iterator = eventList.iterator(); iterator.hasNext(); )
        {
            VoxelEvent event = iterator.next();

            if (event == null || (event.getTime() - currentTime) < 0)
            {
                iterator.remove();
            }

        }
    }
}
