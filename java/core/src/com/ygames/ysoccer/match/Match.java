package com.ygames.ysoccer.match;

public class Match {

    static final int HOME = 0;
    static final int AWAY = 1;

    public final Team team[];
    public Stats[] stats = new Stats[2];
    public boolean ended;

    public Match(Team[] team) {
        this.team = team;
        stats[HOME] = new Stats();
        stats[AWAY] = new Stats();
    }

    public boolean bothComputers() {
        return team[HOME].controlMode == Team.ControlMode.COMPUTER && team[AWAY].controlMode == Team.ControlMode.COMPUTER;
    }

    public class Stats {
        public int goals;
        public int ballPossession = 1;
        public int overallShots;
        public int centeredShots;
        public int cornersWon;
        public int foulsConceded;
        public int yellowCards;
        public int redCards;
    }
}
