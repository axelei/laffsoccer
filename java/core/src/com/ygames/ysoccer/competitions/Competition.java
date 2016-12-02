package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.List;

public abstract class Competition {

    public enum Type {FRIENDLY, LEAGUE, CUP}

    public enum Category {DIY_COMPETITION, PRESET_COMPETITION}

    public String name;
    public String filename;
    public final Type type;
    public Category category;
    public Files files;

    public int numberOfTeams;
    public ArrayList<Team> teams;

    public enum Weather {BY_SEASON, BY_PITCH_TYPE}

    public Weather weather;
    public Month seasonStart;
    public Month seasonEnd;
    public Month currentMonth;
    public Pitch.Type pitchType;
    public int substitutions;
    public int benchSize;
    public MatchSettings.Time time;
    public boolean userPrefersResult;
    public int currentRound;
    public int currentMatch;

    protected Competition(Type type) {
        this.type = type;
        filename = "";
        weather = Weather.BY_SEASON;
        seasonStart = Month.AUGUST;
        seasonEnd = Month.MAY;
        pitchType = Pitch.Type.RANDOM;
        substitutions = 3;
        benchSize = 5;
        time = MatchSettings.Time.DAY;
    }

    public String getMenuTitle() {
        return name;
    }

    public void start(ArrayList<Team> teams) {
        this.teams = new ArrayList<Team>(teams);
    }

    public abstract Match getMatch();

    public boolean isEnded() {
        return true;
    }

    public void restart() {
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

    public void save(String filename) {
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
}
