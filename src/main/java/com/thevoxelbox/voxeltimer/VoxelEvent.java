package com.thevoxelbox.voxeltimer;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * @author giltwist
 */
@SerializableAs("VoxelEvent")
public class VoxelEvent implements ConfigurationSerializable
{
    private String name;
    private long time;
    private Vector location;
    private String world;
    private boolean teleportOnJoin;


    /**
     * @param name
     * @param time
     * @param location
     * @param world
     * @param teleportOnJoin
     */
    public VoxelEvent(final String name, final long time, final Vector location, final String world, final boolean teleportOnJoin)
    {
        this.setName(name);
        this.setTime(time);
        this.location = location;
        this.setWorld(world);
        this.setTeleportOnJoin(teleportOnJoin);
    }

    public static VoxelEvent deserialize(Map<String, Object> data) throws IllegalAccessException
    {
        if (!(data.containsKey("name") && data.containsKey("time") && data.containsKey("location") && data.containsKey("world") && data.containsKey("teleportOnJoin")))
            throw new IllegalAccessException("Not all required data is available.");

        final String deserializedName = (String) data.get("name");
        final Long deserializedTime = (Long) data.get("time");
        final Vector deserializedLocation = (Vector) data.get("location");
        final String deserializedWorld = (String) data.get("world");
        final Boolean deserializedTeleportOnJoin = (Boolean) data.get("teleportOnJoin");

        return new VoxelEvent(deserializedName, deserializedTime, deserializedLocation, deserializedWorld, deserializedTeleportOnJoin);
    }

    @Override
    public Map<String, Object> serialize()
    {
        Map<String, Object> serializedMap = new HashMap<String, Object>();
        serializedMap.put("name", name);
        serializedMap.put("time", time);
        serializedMap.put("location", location);
        serializedMap.put("world", world);
        serializedMap.put("teleportOnJoin", teleportOnJoin);
        return serializedMap;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public String getWorld()
    {
        return world;
    }

    public void setWorld(String world)
    {
        this.world = world;
    }

    public boolean isTeleportOnJoin()
    {
        return teleportOnJoin;
    }

    public void setTeleportOnJoin(boolean teleportOnJoin)
    {
        this.teleportOnJoin = teleportOnJoin;
    }

    public Vector getLocation()
    {
        return this.location;

    }

    public void setLocation(Vector location)
    {
        this.location = location;
    }

    public int getX()
    {
        return location.getBlockX();
    }

    public int getY()
    {
        return location.getBlockY();
    }

    public int getZ()
    {
        return location.getBlockZ();
    }
}
