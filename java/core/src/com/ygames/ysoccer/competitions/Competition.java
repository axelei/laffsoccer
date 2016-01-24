package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.match.Team;

import java.util.Calendar;

public class Competition {
    public String name;
    public Team[] teams;
    public boolean bySeason; // true = by season, false = by pitch type
    public int seasonStart;

    public Competition() {
        bySeason = true;
        seasonStart = Calendar.AUGUST;
    }
}
