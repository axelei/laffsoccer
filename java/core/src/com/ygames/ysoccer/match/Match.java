package com.ygames.ysoccer.match;

public class Match {

    static final int HOME = 0;
    static final int AWAY = 1;

    public final Team team[];

    public Match(Team[] team) {
        this.team = team;
    }
}
