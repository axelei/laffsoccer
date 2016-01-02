package com.ygames.ysoccer.match;

public class Team {

    public enum ControlMode {UNDEFINED, COMPUTER, PLAYER, COACH}

    public String name;
    public String path;
    public String code;
    public String country;
    public String tactics;
    public ControlMode controlMode;

    public Team() {
        controlMode = ControlMode.UNDEFINED;
    }

    @Override
    public boolean equals(Object obj) {
        Team t = (Team) obj;
        return path.equals(t.path) && code.equals(t.code);
    }
}
