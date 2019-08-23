package com.ygames.ysoccer.match;

class MatchState {

    protected int timer;
    protected final int id;
    protected MatchFsm fsm;

    boolean displayControlledPlayer;
    boolean displayBallOwner;
    boolean displayGoalScorer;
    boolean displayTime;
    boolean displayWindVane;
    boolean displayScore;
    boolean displayStatistics;
    boolean displayRadar;
    boolean displayBenchPlayers;
    boolean displayBenchFormation;
    boolean displayTacticsSwitch;

    // convenience references
    protected Match match;
    protected MatchRenderer matchRenderer;

    MatchState(int id, MatchFsm fsm) {
        this.id = id;
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
        fsm.matchKeys.update();
    }

    void checkConditions() {
    }

    boolean checkId(int id) {
        return (this.id == id);
    }

    void render() {
    }

    void replay() {
        fsm.pushAction(MatchFsm.ActionType.FADE_OUT);
        fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_REPLAY);
        fsm.pushAction(MatchFsm.ActionType.FADE_IN);
    }

    void quitMatch() {
        match.quit();
    }
}
