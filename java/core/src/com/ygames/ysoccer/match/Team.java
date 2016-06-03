package com.ygames.ysoccer.match;

public class Team {

    public enum ControlMode {UNDEFINED, COMPUTER, PLAYER, COACH}

    public String name;
    public String path;
    public String country;
    public String tactics;
    public ControlMode controlMode;

    public int won;
    public int drawn;
    public int lost;

    public int goalsFor;
    public int goalsAgainst;
    public int points;

    public Team() {
        controlMode = ControlMode.UNDEFINED;
    }

    @Override
    public boolean equals(Object obj) {
        Team t = (Team) obj;
        return path.equals(t.path);
    }
}
