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
    public String status = "";
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

    public static int generateScore(Team teamA, Team teamB, boolean extraTimeResult) {

        double factor = (teamA.offenseRating() - teamB.defenseRating() + 300) / 60.0;

        int a, b;
        int[] goalsProbability = new int[7];
        for (int goals = 0; goals < 7; goals++) {
            a = Const.goalsProbability[(int) Math.floor(factor)][goals];
            b = Const.goalsProbability[(int) Math.ceil(factor)][goals];
            goalsProbability[goals] = (int) Math.round(a + (b - a) * (factor - Math.floor(factor)));
        }

        goalsProbability[6] = 1000;
        for (int goals = 0; goals <= 5; goals++) {
            goalsProbability[6] = goalsProbability[6] - goalsProbability[goals];
        }

        int r = (int) Math.ceil(1000 * Math.random());
        int sum = 0;
        int goals = -1;
        while (sum < r) {
            goals += 1;
            sum += goalsProbability[goals];
        }

        if (extraTimeResult) {
            return (int) Math.floor(goals / 3);
        } else {
            return goals;
        }
    }
}
