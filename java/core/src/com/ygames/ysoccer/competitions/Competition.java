package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.match.Time;

import java.util.Calendar;

public class Competition {
    public String name;

    public enum Type {
        FRIENDLY, LEAGUE, CUP
    }

    public Type type;

    public int numberOfTeams;
    public Team[] teams;
    public boolean bySeason; // true = by season, false = by pitch type
    public int seasonStart;
    public int seasonEnd;
    public int pitchType;
    public int substitutions;
    public int benchSize;
    public int time;

    public Competition() {
        bySeason = true;
        seasonStart = Calendar.AUGUST;
        seasonEnd = Calendar.MAY;
        pitchType = Pitch.RANDOM;
        substitutions = 3;
        benchSize = 5;
        time = Time.DAY;
    }
}
