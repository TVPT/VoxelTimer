/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxeltimer;

import org.bukkit.World;

/**
 *
 * @author giltwist
 */
public class VoxelEvent {
    public String eventname="";
    public long endtime=0;
    public int x = 0;
    public int y = 0;
    public int z = 0;
    public String world="";
    public boolean tponjoin=true;
    //constructor
    public VoxelEvent(String ename,long etime,int ex,int ey,int ez,String eworld,boolean etponjoin) {
    eventname=ename;
    endtime=etime;
    x = ex;
    y = ey;
    z = ez;
    world=eworld;
    tponjoin=etponjoin;
    }
}
