package com.ygames.ysoccer.match;

public class Match {

    public static final int HOME = 0;
    public static final int AWAY = 1;

    public int team[] = {0, 1};
    public MatchStats[] stats = new MatchStats[2];
    public Result result;
    public boolean includesExtraTime;
    public Result resultAfter90;
    public Result resultAfterPenalties;
    public Result oldResult;
    public int qualified = -1;
    public String status;
    public boolean ended;

    public Match() {
        stats[HOME] = new MatchStats();
        stats[AWAY] = new MatchStats();
    }

    public static class Result {
        public int homeGoals;
        public int awayGoals;

        public Result() {
        }

        public Result(int homeGoals, int awayGoals) {
            this.homeGoals = homeGoals;
            this.awayGoals = awayGoals;
        }
    }
}
