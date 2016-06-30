package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.match.Time;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class Competition {
    public String name;
    public String longName;
    public String filename;
    public String path;
    public String absolutePath;

    public enum Type {
        FRIENDLY, LEAGUE, CUP
    }

    public Type type;

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
    public int pitchType;
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
        pitchType = Pitch.RANDOM;
        substitutions = 3;
        benchSize = 5;
        time = Time.DAY;
    }

    public String getMenuTitle() {
        return longName;
    }

    public void start(ArrayList<Team> teams) {
        this.teams = (ArrayList<Team>) teams.clone();
    }

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
                Team team = Assets.json.fromJson(Team.class, teamFile.readString());
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

    public void save() {
        String saveName = filename;
        switch (category) {
            case DIY_COMPETITION:
                saveName += ".diy.json";
                break;
            case PRESET_COMPETITION:
                saveName += ".preset.json";
                break;
        }
        FileHandle fileHandle = Assets.savesFolder.child(saveName);
        Assets.json.toJson(this, Competition.class, fileHandle);
    }

}
