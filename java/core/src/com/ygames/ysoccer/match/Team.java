package com.ygames.ysoccer.match;

public class Team {

    public enum ControlMode {UNDEFINED, COMPUTER, PLAYER, COACH}

    public String name;
    public String code;
    public String country;
    public String tactics;
    public ControlMode controlMode;

    public Team() {
        controlMode = ControlMode.UNDEFINED;
    }
}
