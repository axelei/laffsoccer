package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;

public class TeamList extends ArrayList<Team> {

    public void addTeam(Team team) {
        if (indexOf(null) == -1) {
            add(team);
        } else {
            set(indexOf(null), team);
        }
    }

    public void removeTeam(Team team) {
        set(indexOf(team), null);
    }

    public int numberOfTeams() {
        int count = 0;
        for (Team team : this) {
            if (team != null) count++;
        }
        return count;
    }

    public void removeNullValues() {
        while (indexOf(null) != -1) {
            if (indexOf(null) == size() - 1) {
                remove(indexOf(null));
            } else {
                set(indexOf(null), get(size() - 1));
                remove(size() - 1);
            }
        }
    }
}
