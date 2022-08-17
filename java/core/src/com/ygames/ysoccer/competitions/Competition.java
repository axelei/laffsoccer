package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Referee;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.ygames.ysoccer.match.Const.BASE_TEAM;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Team.ControlMode.COMPUTER;
import static java.lang.Math.min;

public abstract class Competition {

    public enum Type {FRIENDLY, LEAGUE, CUP, TOURNAMENT, TEST_MATCH}

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
    public ArrayList<PlayerSanctions> sanctionsList;
    final private Comparator<Scorer> scorerComparator;

    protected Competition(Type type) {
        this.type = type;
        filename = "";
        teams = new ArrayList<>();
        weather = Weather.BY_SEASON;
        seasonStart = Month.AUGUST;
        seasonEnd = Month.MAY;
        pitchType = Pitch.Type.RANDOM;
        substitutions = 3;
        benchSize = 5;
        time = MatchSettings.Time.DAY;
        scorers = new ArrayList<>();
        scorerComparator = new ScorerComparator();
        sanctionsList = new ArrayList<>();
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

        JsonValue rawSanctionsList = jsonData.get("sanctions");
        if (rawSanctionsList != null) {
            JsonValue rawPlayerSanctions = rawSanctionsList.child();
            while (rawPlayerSanctions != null) {
                Team team = teams.get(rawPlayerSanctions.getInt("team"));
                Player player = team.players.get(rawPlayerSanctions.getInt("player"));
                sanctionsList.add(new PlayerSanctions(player, rawPlayerSanctions.getInt("yellows"), rawPlayerSanctions.getInt("suspensions")));
                rawPlayerSanctions = rawPlayerSanctions.next();
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
        json.writeArrayStart("sanctions");
        for (PlayerSanctions playerSanctions : sanctionsList) {
            if (playerSanctions.yellows == 0 && playerSanctions.suspensions == 0) continue;
            json.writeObjectStart();
            json.writeValue("team", teams.indexOf(playerSanctions.player.team));
            json.writeValue("player", playerSanctions.player.team.players.indexOf(playerSanctions.player));
            json.writeValue("yellows", playerSanctions.yellows);
            json.writeValue("suspensions", playerSanctions.suspensions);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    public String getMenuTitle() {
        return name;
    }

    public void start(ArrayList<Team> teams) {
        this.teams = new ArrayList<>(teams);
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

    public boolean playExtraTime() {
        return false;
    }

    public boolean playPenalties() {
        return false;
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

    public MatchSettings.Time getTime() {
        return time;
    }

    public Pitch.Type getPitchType() {
        int p;

        if (weather == Weather.BY_SEASON) {
            p = -1;
            int n = EMath.rand(0, 99);
            int tot = 0;
            do {
                p = p + 1;
                tot = tot + Pitch.probabilityByMonth[currentMonth.ordinal()][p];
            } while (tot <= n);

            return Pitch.Type.values()[p];
        }
        // BY_PITCH_TYPE
        else {
            if (pitchType == Pitch.Type.RANDOM) {
                return Pitch.random();
            } else {
                return pitchType;
            }
        }
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

    private static class ScorerComparator implements Comparator<Scorer> {

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
            int target = 1 + EMath.floor(teamWeight * Math.random());
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
        dropSuspensions();
        updateSanctionsList();
    }

    public Team getMatchWinner() {
        return null;
    }

    public Team getFinalWinner() {
        return null;
    }

    public Team getFinalRunnerUp() {
        return null;
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

    public static class PlayerSanctions {
        public Player player;
        public int yellows;
        public int suspensions;

        PlayerSanctions(Player player, int yellows, int suspensions) {
            this.player = player;
            this.yellows = yellows;
            this.suspensions = suspensions;
        }
    }

    private void dropSuspensions() {
        Team homeTeam = getTeam(HOME);
        Team awayTeam = getTeam(AWAY);
        for (PlayerSanctions playerCards : sanctionsList) {
            if (playerCards.player.team == homeTeam || playerCards.player.team == awayTeam) {
                if (playerCards.suspensions > 0) {
                    playerCards.suspensions -= 1;
                }
            }
        }
    }

    private void updateSanctionsList() {
        Map<Player, Referee.PenaltyCard> penaltyCards = getMatch().getReferee().getPenaltyCards();
        for (Map.Entry<Player, Referee.PenaltyCard> entry : penaltyCards.entrySet()) {
            updatePlayerSanctions(entry.getKey(), entry.getValue());
        }
    }

    private void updatePlayerSanctions(Player player, Referee.PenaltyCard penaltyCard) {
        PlayerSanctions playerCards = searchPlayerSanctions(player);
        if (playerCards == null) {
            playerCards = new PlayerSanctions(player, 0, 0);
            sanctionsList.add(playerCards);
        }

        switch (penaltyCard) {
            case YELLOW:
                if(oneToSuspension(playerCards.yellows)) {
                    playerCards.suspensions += 1;
                }
                playerCards.yellows += 1;
                break;

            case YELLOW_PLUS_RED:
                if(oneToSuspension(playerCards.yellows)) {
                    playerCards.suspensions += 1;
                }
                playerCards.yellows += 1;
                playerCards.suspensions += 1;
                break;

            case DOUBLE_YELLOW:
            case RED:
                playerCards.suspensions += 1;
                break;
        }
    }

    public PlayerSanctions searchPlayerSanctions(Player player) {
        for (PlayerSanctions playerSanctions : sanctionsList) {
            if (playerSanctions.player == player) {
                return playerSanctions;
            }
        }
        return null;
    }

    public boolean isPlayerCautioned(Player player){
        PlayerSanctions playerSanctions = searchPlayerSanctions(player);
        return oneToSuspension(playerSanctions.yellows);
    }

    protected boolean oneToSuspension(int yellows) {
        return false;
    }
}