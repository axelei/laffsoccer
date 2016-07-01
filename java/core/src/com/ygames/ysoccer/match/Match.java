package com.ygames.ysoccer.match;

public class Match {

    public static final int HOME = 0;
    public static final int AWAY = 1;

    public int team[] = {0, 1};
    public MatchStats[] stats = new MatchStats[2];
    public boolean ended;

    public Match() {
        stats[HOME] = new MatchStats();
        stats[AWAY] = new MatchStats();
    }
}
