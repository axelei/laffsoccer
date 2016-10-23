package com.ysoccer.android.ysdemo.match;

import java.util.Comparator;

public class TeamComparator implements Comparator<Team> {

    @Override
    public int compare(Team team1, Team team2) {
        return team1.name.compareTo(team2.name);
    }
}
