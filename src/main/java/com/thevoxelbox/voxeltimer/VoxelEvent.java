package com.thevoxelbox.voxeltimer;

/**
 * 
 * @author giltwist
 */
public class VoxelEvent {
    public String name;
    public long endtime;
    public int x;
    public int y;
    public int z;
    public String world;
    public boolean teleportOnJoin;

    /**
     * @param name
     * @param time
     * @param x
     * @param y
     * @param z
     * @param world
     * @param teleportOnJoin
     */
    public VoxelEvent(final String name, final long time, final int x, final int y, final int z, final String world, final boolean teleportOnJoin) {
        this.name = name;
        this.endtime = time;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.teleportOnJoin = teleportOnJoin;
    }
}
