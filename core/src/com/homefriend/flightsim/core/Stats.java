package com.homefriend.flightsim.core;

import com.homefriend.flightsim.FlightSim;

public class Stats {
    public static Stats inst = new Stats();
    public int lives = 3;
    public int score = 0;
    public int hiscore = 0;
    public float density = 1f;
    private Stats(){

    }
    public void init(FlightSim game){
        Stats.inst = new Stats();
        Stats.inst.hiscore = game.prefs.getInteger("HISCORE", 8000);
        Stats.inst.density = game.getDensity();
    }
}
