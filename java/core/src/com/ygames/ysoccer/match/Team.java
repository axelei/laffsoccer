package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
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
    public String stadium;
    public Coach coach;

    public String tactics;
    public ControlMode controlMode;

    public int kitIndex;
    public List<Kit> kits;
    public static final int MIN_KITS = 3;
    public static final int MAX_KITS = 5;

    int index; // 0=HOME, 1=AWAY

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

    public Player newPlayer() {
        if (players.size() == Const.FULL_TEAM) {
            return null;
        }

        Player player = new Player();
        player.name = "";
        player.shirtName = "";
        player.nationality = country;
        player.role = Player.Role.GOALKEEPER;

        // number
        for (int i = 1; i <= Const.FULL_TEAM; i++) {
            boolean used = false;
            for (Player ply : players) {
                if (Integer.parseInt(ply.number) == i) {
                    used = true;
                }
            }
            if (!used) {
                player.number = "" + i;
                break;
            }
        }
        player.hairColor = "BLACK";
        player.hairStyle = "SMOOTH_A";
        player.skills = new Player.Skills();
        players.add(player);
        return player;
    }

    public boolean deletePlayer(Player player) {
        if (players.size() > Const.BASE_TEAM) {
            return players.remove(player);
        }
        return false;
    }

    public Kit newKit() {
        if (kits.size() == MAX_KITS) {
            return null;
        }
        Kit kit = new Kit();
        kits.add(kit);
        return kit;
    }

    public boolean deleteKit() {
        if (kits.size() > MIN_KITS) {
            return kits.remove(kits.get(kits.size() - 1));
        }
        return false;
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
        int teamFinishing = 0;
        for (int pos = 0; pos < Const.TEAM_SIZE; pos++) {
            Player ply = playerAtPosition(pos);

            teamFinishing += ply.skills.heading + ply.skills.shooting + ply.skills.finishing;

            switch (ply.role) {
                case RIGHT_WINGER:
                case LEFT_WINGER:
                    teamFinishing += 10;
                    break;
                case MIDFIELDER:
                    teamFinishing += 5;
                    break;
                case ATTACKER:
                    teamFinishing += 30;
                    break;
            }
        }

        for (int g = 1; g <= goals; g++) {
            int target = 1 + Emath.floor(teamFinishing * Math.random());
            int sum = teamFinishing;
            for (int pos = 0; pos < Const.TEAM_SIZE; pos++) {
                Player ply = playerAtPosition(pos);

                sum = sum - ply.skills.heading - ply.skills.shooting - ply.skills.finishing;

                switch (ply.role) {
                    case RIGHT_WINGER:
                    case LEFT_WINGER:
                        sum -= 10;
                        break;
                    case MIDFIELDER:
                        sum -= 5;
                        break;
                    case ATTACKER:
                        sum -= 30;
                        break;
                }

                if (sum < target) {
                    ply.goals += 1;
                    break;
                }
            }
        }
    }

    public Player playerAtPosition(int pos) {
        return playerAtPosition(pos, null);
    }

    public Player playerAtPosition(int pos, Tactics tcs) {
        if (tcs == null) {
            tcs = Assets.tactics[getTacticsIndex()];
        }
        if (pos < players.size()) {
            int ply = (pos < Const.TEAM_SIZE) ? Tactics.order[tcs.basedOn][pos] : pos;
            return players.get(ply);
        } else {
            return null;
        }
    }

    public int playerIndexAtPosition(int pos) {
        if (pos < players.size()) {
            int baseTactics = Assets.tactics[getTacticsIndex()].basedOn;
            return (pos < Const.TEAM_SIZE) ? Tactics.order[baseTactics][pos] : pos;
        } else {
            return -1;
        }
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

    // TODO: replace with custom serialization
    public int getTacticsIndex() {
        for (int i = 0; i < Tactics.codes.length; i++) {
            if (Tactics.codes[i].equals(tactics)) {
                return i;
            }
        }
        return -1;
    }

    public static void kitAutoSelection(Team homeTeam, Team awayTeam) {
        for (int i = 0; i < homeTeam.kits.size(); i++) {
            for (int j = 0; j < awayTeam.kits.size(); j++) {
                Kit homeKit = homeTeam.kits.get(i);
                Kit awayKit = awayTeam.kits.get(j);
                float difference = Kit.colorDifference(homeKit, awayKit);
                if (difference > 45) {
                    homeTeam.kitIndex = i;
                    awayTeam.kitIndex = j;
                    return;
                }
            }
        }
    }
}
