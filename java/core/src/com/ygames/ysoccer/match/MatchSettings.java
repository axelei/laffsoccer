package com.ygames.ysoccer.match;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.math.Emath;

public class MatchSettings {

    public int time;

    public MatchSettings(Competition competition) {
        this.time = competition.time;
    }

    public void rotateTime(int direction) {
        time = Emath.rotate(time, Time.DAY, Time.NIGHT, direction);
    }
}
