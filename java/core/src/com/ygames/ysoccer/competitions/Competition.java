package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.match.Team;

public class Competition {
    public String name;
    public Team[] teams;
    public boolean bySeason; // true = by season, false = by pitch type

    public Competition() {
        bySeason = true;
    }
}
