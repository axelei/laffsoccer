package com.ysoccer.android.ysdemo.match;

public class State {

    protected int timer;
    protected int id;

    int getId() {
        return id;
    }

    void doActions() {
        timer += 1;
    }

    State checkConditions() {
        return null;
    }

    void entryActions() {
        timer = 0;
    }

    void exitActions() {
    }

    boolean checkId(int id) {
        return (this.id == id);
    }

    @Override
    public String toString() {
        return id + ":" + timer;
    }
}
