package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;

class MatchState {

    protected int timer;
    protected final MatchFsm.Id id;
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

    MatchState(MatchFsm.Id id, MatchFsm fsm) {
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

    boolean checkId(MatchFsm.Id id) {
        return (this.id == id);
    }

    void render() {
    }

    void replay() {
        fsm.pushAction(FADE_OUT);
        fsm.pushAction(HOLD_FOREGROUND, STATE_REPLAY);
        fsm.pushAction(FADE_IN);
    }

    void quitMatch() {
        match.quit();
    }
}
