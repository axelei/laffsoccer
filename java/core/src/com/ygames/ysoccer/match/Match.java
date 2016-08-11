package com.ygames.ysoccer.match;

public class Match {

    public static final int HOME = 0;
    public static final int AWAY = 1;

    public int team[] = {0, 1};
    public MatchStats[] stats = new MatchStats[2];
    public MatchStats[] statsAfter90;
    public MatchStats[] oldStats;
    public boolean includesExtraTime;
    public boolean ended;
    public String status;
    public int qualified = -1;

    public Match() {
        stats[HOME] = new MatchStats();
        stats[AWAY] = new MatchStats();
    }
}
