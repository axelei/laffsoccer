package com.ygames.ysoccer.competitions;

public class Friendly extends Competition {

    public Friendly() {
        numberOfTeams = 2;
    }

    public Type getType() {
        return Type.FRIENDLY;
    }
}
