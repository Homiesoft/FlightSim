package com.homefriend.flightsim.core;

public class Prefs {
    public static Prefs inst = new Prefs();
    public float sndVol = 1.0f;
    public float musVol = 1.0f;
    public boolean soundMute = false;
    public boolean musicMute = false;
    public boolean vert = false;
    private Prefs(){
    }
}
