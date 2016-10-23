package com.ysoccer.android.ysdemo.match;

public class Goal {

    enum Type {NORMAL, OWN_GOAL, PENALTY}

    Player player;
    int minute;
    Type type;

    public Goal(Player player, int minute, Type type) {
        this.player = player;
        this.minute = minute;
        this.type = type;
    }

}
