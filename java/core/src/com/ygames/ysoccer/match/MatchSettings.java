package com.ygames.ysoccer.match;

import com.ygames.ysoccer.competitions.Competition;

public class MatchSettings {

    public int time;

    public MatchSettings(Competition competition) {
        this.time = competition.time;
    }
}
