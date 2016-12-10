package com.ygames.ysoccer.match;

class MatchState {

    protected int timer;
    protected int id;
    protected MatchFsm fsm;

    boolean displayControlledPlayer;
    boolean displayBallOwner;
    boolean displayGoalScorer;
    boolean displayTime;
    boolean displayWindVane;
    boolean displayScore;
    boolean displayStatistics;
    boolean displayRadar;

    // convenience references
    protected Match match;
    protected MatchRenderer matchRenderer;

    MatchState(MatchFsm fsm) {
        this.fsm = fsm;
        this.match = fsm.getMatch();
        this.matchRenderer = fsm.getMatchRenderer();
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
