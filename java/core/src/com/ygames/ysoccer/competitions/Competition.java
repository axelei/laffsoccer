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
    public String path;
    public String absolutePath;

    public enum Type {
        FRIENDLY, LEAGUE, CUP
    }

    public Type type;

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

    public void restart() {
    }

    public boolean isEnded() {
        return true;
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
}
