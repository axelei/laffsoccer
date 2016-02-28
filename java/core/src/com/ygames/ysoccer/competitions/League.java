package com.ygames.ysoccer.competitions;

public class League extends Competition {

    public int rounds;
    public int pointsForAWin;

    public League() {
        type = Type.LEAGUE;
        numberOfTeams = 2;
        rounds = 1;
        pointsForAWin = 3;
    }
}
