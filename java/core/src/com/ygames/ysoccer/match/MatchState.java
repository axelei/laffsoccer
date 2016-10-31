package com.ygames.ysoccer.match;

class MatchState {

    protected int timer;
    protected int id;
    protected MatchCore match;

    MatchState(MatchCore match) {
        this.match = match;
    }

    void entryActions() {
        timer = 0;
    }

    void onResume() {
    }

    void onPause() {
    }

    void doActions(float deltaTime) {
        timer += 1;
    }

    void checkConditions() {
    }

    boolean checkId(int id) {
        return (this.id == id);
    }

    void render() {
    }
}
