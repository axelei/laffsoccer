package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.match.Time;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class Competition {
    public String name;
    public String filename;
    public String path;
    public String absolutePath;

    public enum Type {
        FRIENDLY, LEAGUE, CUP
    }

    public enum Category {
        DIY_COMPETITION,
        PRESET_COMPETITION
    }

    public Category category;

    public int numberOfTeams;
    public ArrayList<Team> teams;
    public boolean bySeason; // true = by season, false = by pitch type
    public int seasonStart;
    public int seasonEnd;
    public int currentMonth;
    public Pitch.Type pitchType;
    public int substitutions;
    public int benchSize;
    public int time;
    public boolean userPrefersResult;
    public int currentRound;
    public int currentMatch;

    public Competition() {
        filename = "";
        path = "";
        bySeason = true;
        seasonStart = Calendar.AUGUST;
        seasonEnd = Calendar.MAY;
        pitchType = Pitch.Type.RANDOM;
        substitutions = 3;
        benchSize = 5;
        time = Time.DAY;
    }

    public abstract Type getType();

    public String getMenuTitle() {
        return name;
    }

    public void start(ArrayList<Team> teams) {
        this.teams = (ArrayList<Team>) teams.clone();
    }

    public abstract Match getMatch();

    public boolean isEnded() {
        return true;
    }

    public void restart() {
    }

    public ArrayList<Team> loadTeams() {
        ArrayList<Team> teamList = new ArrayList<Team>();
        for (Team teamStub : teams) {
            FileHandle teamFile = Assets.teamsFolder.child(teamStub.path);
            if (teamFile.exists()) {
                Team team = Assets.json.fromJson(Team.class, teamFile.readString("UTF-8"));
                team.path = teamStub.path;
                team.controlMode = Team.ControlMode.COMPUTER;
                teamList.add(team);
            }
        }
        return teamList;
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

    public String getBySeasonLabel() {
        return bySeason ? "SEASON" : "PITCH TYPE";
    }

    public Pitch.Type resolvePitchType() {
        int p;

        if (bySeason) {
            p = -1;
            int n = Emath.rand(0, 99);
            int tot = 0;
            do {
                p = p + 1;
                tot = tot + Pitch.probabilityByMonth[currentMonth][p];
            } while (tot <= n);
        }
        // by pitch type
        else {
            if (pitchType == Pitch.Type.RANDOM) {
                p = Emath.rand(Pitch.Type.FROZEN.ordinal(), Pitch.Type.WHITE.ordinal());
            } else {
                p = pitchType.ordinal();
            }
        }

        return Pitch.Type.values()[p];
    }

    public void save() {
        FileHandle fileHandle = Assets.savesFolder.child(getCategoryFolder()).child(filename + ".json");
        fileHandle.writeString(Assets.json.toJson(this, Competition.class), false, "UTF-8");
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
}
