package com.ygames.ysoccer.match;

abstract class State {

    int timer;
    int id;

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
