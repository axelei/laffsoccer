package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

import java.util.Comparator;
import java.util.List;

public class Team {

    public enum Type {CLUB, NATIONAL}

    public enum ControlMode {UNDEFINED, COMPUTER, PLAYER, COACH}

    public String name;
    public Type type;
    public String path;
    public String country;
    public String city;
    public String tactics;
    public ControlMode controlMode;

    public List<Player> players;

    public int won;
    public int drawn;
    public int lost;

    public int goalsFor;
    public int goalsAgainst;
    public int points;

    public Team() {
        controlMode = ControlMode.UNDEFINED;
    }

    @Override
    public boolean equals(Object obj) {
        Team t = (Team) obj;
        return path.equals(t.path);
    }

    public static class CompareByStats implements Comparator<Team> {

        @Override
        public int compare(Team o1, Team o2) {
            // by points
            if (o1.points != o2.points) {
                return o2.points - o1.points;
            }

            // by goals diff
            int diff1 = o1.goalsFor - o1.goalsAgainst;
            int diff2 = o2.goalsFor - o2.goalsAgainst;
            if (diff1 != diff2) {
                return diff2 - diff1;
            }

            // by scored goals
            if (o1.goalsFor != o2.goalsFor) {
                return o2.goalsFor - o1.goalsFor;
            }

            // by names
            return o1.name.compareTo(o2.name);
        }
    }

    public void updateStats(int goalsFor, int goalsAgainst, int pointsForAWin) {
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
        if (goalsFor > goalsAgainst) {
            won += 1;
            points += pointsForAWin;
        } else if (goalsFor == goalsAgainst) {
            drawn += 1;
            points += 1;
        } else {
            lost += 1;
        }
    }

    public void generateScorers(int goals) {
        for (int g = 1; g <= goals; g++) {
            int i = Emath.floor(11 * Math.random());
            players.get(i).goals++;
        }
    }

    public Player playerAtPosition(int p) {
        // TODO: to be implemented
        return p < players.size() ? players.get(p) : null;
    }

    public int defenseRating() {
        int defense = 0;
        for (int p = 0; p < 11; p++) {
            defense += playerAtPosition(p).getDefenseRating();
        }
        return defense;
    }

    public int offenseRating() {
        int offense = 0;
        for (int p = 0; p < 11; p++) {
            offense += playerAtPosition(p).getOffenseRating();
        }
        return offense;
    }

}
