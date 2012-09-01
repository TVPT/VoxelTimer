package com.thevoxelbox.voxeltimer;

/**
 * 
 * @author giltwist
 */
public class VoxelEvent {
    public String eventname = "";
    public long endtime = 0;
    public int x = 0;
    public int y = 0;
    public int z = 0;
    public String world = "";
    public boolean tponjoin = true;

    /**
     * @param ename
     * @param etime
     * @param ex
     * @param ey
     * @param ez
     * @param eworld
     * @param etponjoin
     */
    public VoxelEvent(final String ename, final long etime, final int ex, final int ey, final int ez, final String eworld, final boolean etponjoin) {
        this.eventname = ename;
        this.endtime = etime;
        this.x = ex;
        this.y = ey;
        this.z = ez;
        this.world = eworld;
        this.tponjoin = etponjoin;
    }
}
