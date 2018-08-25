package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ygames.ysoccer.match.Const.BASE_TEAM;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Team.ControlMode.COMPUTER;
import static java.lang.Math.min;

public abstract class Competition {

    public enum Type {FRIENDLY, LEAGUE, CUP, TOURNAMENT}

    public enum Category {DIY_COMPETITION, PRESET_COMPETITION}

    public enum Weather {BY_SEASON, BY_PITCH_TYPE}

    public enum AwayGoals {OFF, AFTER_90_MINUTES, AFTER_EXTRA_TIME}

    public String name;
    public String filename;
    public final Type type;
    public Category category;
    public int numberOfTeams;
    public Files files;

    public MatchSettings.Time time;
    public Weather weather;
    public Month seasonStart;
    public Month seasonEnd;
    public Month currentMonth;
    public Pitch.Type pitchType;
    public int substitutions;
    public int benchSize;
    public boolean userPrefersResult;
    public int currentRound;
    public int currentMatch;

    public List<Team> teams;
    public ArrayList<Scorer> scorers;
    private Comparator<Scorer> scorerComparator;

    protected Competition(Type type) {
        this.type = type;
        filename = "";
        teams = new ArrayList<Team>();
        weather = Weather.BY_SEASON;
        seasonStart = Month.AUGUST;
        seasonEnd = Month.MAY;
        pitchType = Pitch.Type.RANDOM;
        substitutions = 3;
        benchSize = 5;
        time = MatchSettings.Time.DAY;
        scorers = new ArrayList<Scorer>();
        scorerComparator = new ScorerComparator();
    }

    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        filename = jsonData.getString("filename", "");
        category = json.readValue("category", Category.class, jsonData);
        numberOfTeams = jsonData.getInt("numberOfTeams");
        files = json.readValue("files", Files.class, jsonData);

        time = json.readValue("time", MatchSettings.Time.class, jsonData);
        weather = json.readValue("weather", Weather.class, jsonData);
        if (weather == Weather.BY_SEASON) {
            seasonStart = json.readValue("seasonStart", Month.class, jsonData);
            seasonEnd = json.readValue("seasonEnd", Month.class, jsonData);
            currentMonth = json.readValue("currentMonth", Month.class, jsonData);
        } else {
            pitchType = json.readValue("pitchType", Pitch.Type.class, jsonData);
        }
        substitutions = jsonData.getInt("substitutions", 3);
        benchSize = jsonData.getInt("benchSize", 5);
        userPrefersResult = jsonData.getBoolean("userPrefersResult", false);
        currentRound = jsonData.getInt("currentRound", 0);
        currentMatch = jsonData.getInt("currentMatch", 0);

        Team[] teamsArray = json.readValue("teams", Team[].class, jsonData);
        if (teamsArray != null) {
            Collections.addAll(teams, teamsArray);
        }

        JsonValue rawScorers = jsonData.get("scorers");
        if (rawScorers != null) {
            JsonValue rawScorer = rawScorers.child();
            while (rawScorer != null) {
                Team team = teams.get(rawScorer.getInt("team"));
                Player player = team.players.get(rawScorer.getInt("player"));
                scorers.add(new Scorer(player, rawScorer.getInt("goals")));
                rawScorer = rawScorer.next();
            }
        }
    }

    public void write(Json json) {
        json.writeValue("name", name);
        if (filename.length() > 0) {
            json.writeValue("filename", filename);
        }
        json.writeValue("category", category);
        json.writeValue("numberOfTeams", numberOfTeams);

        json.writeValue("time", time);
        json.writeValue("weather", weather);
        if (weather == Weather.BY_SEASON) {
            json.writeValue("seasonStart", seasonStart);
            json.writeValue("seasonEnd", seasonEnd);
            json.writeValue("currentMonth", currentMonth);
        } else {
            json.writeValue("pitchType", pitchType);
        }
        json.writeValue("substitutions", substitutions);
        json.writeValue("benchSize", benchSize);
        json.writeValue("userPrefersResult", userPrefersResult);
        json.writeValue("currentRound", currentRound);
        json.writeValue("currentMatch", currentMatch);
        json.writeValue("teams", teams, Team[].class, Team.class);
        json.writeArrayStart("scorers");
        for (Scorer scorer : scorers) {
            json.writeObjectStart();
            json.writeValue("team", teams.indexOf(scorer.player.team));
            json.writeValue("player", scorer.player.team.players.indexOf(scorer.player));
            json.writeValue("goals", scorer.goals);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    public String getMenuTitle() {
        return name;
    }

    public void start(ArrayList<Team> teams) {
        this.teams = new ArrayList<Team>(teams);
    }

    public abstract Match getMatch();

    public Team getTeam(int side) {
        return teams.get(getTeamIndex(side));
    }

    public int getTeamIndex(int side) {
        return getMatch().teams[side];
    }

    public boolean bothComputers() {
        return getTeam(HOME).controlMode == COMPUTER
                && getTeam(AWAY).controlMode == COMPUTER;
    }

    public boolean isEnded() {
        return true;
    }

    public void restart() {
        userPrefersResult = false;
        scorers.clear();
    }

    public static String getCategoryLabel(Category category) {
        String label = "";
        switch (category) {
            case DIY_COMPETITION:
                label = "DIY COMPETITION";
                break;

            case PRESET_COMPETITION:
                label = "PRESET COMPETITION";
                break;
        }
        return label;
    }

    public static String getWarningLabel(Category category) {
        String label = "";
        switch (category) {
            case DIY_COMPETITION:
                label = "YOU ARE ABOUT TO LOSE CURRENT DIY COMPETITION";
                break;

            case PRESET_COMPETITION:
                label = "YOU ARE ABOUT TO LOSE CURRENT PRESET COMPETITION";
                break;
        }
        return label;
    }

    public String getWeatherLabel() {
        return weather == Weather.BY_SEASON ? "SEASON" : "PITCH TYPE";
    }

    public String getAwayGoalsLabel(AwayGoals awayGoals) {
        switch (awayGoals) {
            case OFF:
                return "OFF";
            case AFTER_90_MINUTES:
                return "AFTER 90 MINS";
            case AFTER_EXTRA_TIME:
                return "AFTER EXTRA TIME";
            default:
                throw new GdxRuntimeException("Unknown AwayGoals value");
        }
    }

    public Pitch.Type resolvePitchType() {
        int p;

        if (weather == Weather.BY_SEASON) {
            p = -1;
            int n = Emath.rand(0, 99);
            int tot = 0;
            do {
                p = p + 1;
                tot = tot + Pitch.probabilityByMonth[currentMonth.ordinal()][p];
            } while (tot <= n);
        }
        // BY_PITCH_TYPE
        else {
            if (pitchType == Pitch.Type.RANDOM) {
                p = Emath.rand(Pitch.Type.FROZEN.ordinal(), Pitch.Type.WHITE.ordinal());
            } else {
                p = pitchType.ordinal();
            }
        }

        return Pitch.Type.values()[p];
    }

    public static Competition load(FileHandle fileHandle) {
        return Assets.json.fromJson(Competition.class, fileHandle);
    }

    public void saveAndSetFilename(String filename) {
        this.filename = "";
        save(Assets.savesFolder.child(getCategoryFolder()).child(filename + ".json"));
        this.filename = filename;
    }

    public void save(FileHandle fileHandle) {
        fileHandle.writeString(Assets.json.toJson(this, Competition.class), false, "UTF-8");
    }

    public String getNewFilename() {
        String newFilename = name;
        int i = 2;
        while (Assets.savesFolder.child(getCategoryFolder()).child(newFilename + ".json").exists()) {
            newFilename = name + " (" + i + ")";
            i++;
        }
        return newFilename;
    }

    public String getCategoryFolder() {
        switch (category) {
            case DIY_COMPETITION:
                return "DIY";

            case PRESET_COMPETITION:
                return "PRESET";

            default:
                throw new GdxRuntimeException("Unknown category");
        }
    }

    public static class Files {
        public String folder;
        public String league;
        public List<String> teams;
    }

    public static class Scorer {
        public Player player;
        public int goals;

        Scorer(Player player, int goals) {
            this.player = player;
            this.goals = goals;
        }
    }

    private class ScorerComparator implements Comparator<Scorer> {

        @Override
        public int compare(Scorer o1, Scorer o2) {
            // by goals
            if (o1.goals != o2.goals) {
                return o2.goals - o1.goals;
            }

            // by shirt names
            if (!o1.player.shirtName.equals(o2.player.shirtName)) {
                return o1.player.shirtName.compareTo(o2.player.shirtName);
            }

            // by team name
            return o1.player.team.name.compareTo(o2.player.team.name);
        }
    }

    private void sortScorerList() {
        Collections.sort(scorers, scorerComparator);
    }


    public void generateScorers(Team team, int goals) {
        int teamSize = min(team.players.size(), Const.TEAM_SIZE + benchSize);
        int teamWeight = 0;
        for (int playerIndex = 0; playerIndex < teamSize; playerIndex++) {
            Player player = team.players.get(playerIndex);
            teamWeight += player.getScoringWeight();
        }

        for (int i = 0; i < goals; i++) {
            int target = 1 + Emath.floor(teamWeight * Math.random());
            int sum = teamWeight;
            for (int playerIndex = 0; playerIndex < teamSize; playerIndex++) {
                Player player = team.players.get(playerIndex);

                sum -= player.getScoringWeight();

                if (sum < target) {
                    addGoal(player);
                    break;
                }
            }
        }
    }

    public void addGoal(Player player) {
        Scorer scorer = searchScorer(player);
        if (scorer == null) {
            scorers.add(new Scorer(player, 1));
        } else {
            scorer.goals++;
        }
        sortScorerList();
    }

    private Scorer searchScorer(Player player) {
        for (Scorer scorer : scorers) {
            if (scorer.player == player) {
                return scorer;
            }
        }
        return null;
    }

    public int getScorerGoals(Player player) {
        Scorer scorer = searchScorer(player);
        return scorer == null ? 0 : scorer.goals;
    }

    public void matchInterrupted() {
    }

    public void matchCompleted() {
    }

    public class ComparatorByPlayersValue implements Comparator<Integer> {

        @Override
        public int compare(Integer teamIndex1, Integer teamIndex2) {
            Team team1 = teams.get(teamIndex1), team2 = teams.get(teamIndex2);
            int v1 = 0, v2 = 0;
            for (int i = 0; i < BASE_TEAM; i++) {
                v1 += team1.playerAtPosition(i).getValue();
                v2 += team2.playerAtPosition(i).getValue();
            }

            // by players value
            if (v1 != v2) {
                return v2 - v1;
            }

            // by team name
            return team1.name.compareTo(team2.name);
        }
    }
}
