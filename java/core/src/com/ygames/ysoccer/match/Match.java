package com.ygames.ysoccer.match;

public class Match {

    public static final int HOME = 0;
    public static final int AWAY = 1;

    public Team team[];
    public MatchStats[] stats = new MatchStats[2];
    public boolean ended;

    public Match() {
    }

    public Match(Team[] team) {
        this.team = team;
        stats[HOME] = new MatchStats();
        stats[AWAY] = new MatchStats();
    }

    public boolean bothComputers() {
        return team[HOME].controlMode == Team.ControlMode.COMPUTER && team[AWAY].controlMode == Team.ControlMode.COMPUTER;
    }
}
