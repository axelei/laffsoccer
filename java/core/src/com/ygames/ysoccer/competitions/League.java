package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.framework.Assets;

public class League extends Competition {

    public int rounds;
    public int pointsForAWin;

    public League() {
        type = Type.LEAGUE;
        numberOfTeams = 2;
        rounds = 1;
        pointsForAWin = 3;
    }

    public void init() {
        numberOfTeams = teams.size();
        absolutePath = Assets.teamsFolder.child(path).path();
    }
}
