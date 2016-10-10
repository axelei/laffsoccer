package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.match.Time;

public class Friendly extends Competition {

    public Friendly() {
        numberOfTeams = 2;
        time = Time.RANDOM;
    }

    public Type getType() {
        return Type.FRIENDLY;
    }
}
