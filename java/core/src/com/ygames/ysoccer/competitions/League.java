package com.ygames.ysoccer.competitions;

public class League extends Competition {

    public int numberOfTeams;
    public int rounds;
    public int pointsForAWin;

    public League() {
        numberOfTeams = 2;
        rounds = 1;
        pointsForAWin = 3;
    }
}
