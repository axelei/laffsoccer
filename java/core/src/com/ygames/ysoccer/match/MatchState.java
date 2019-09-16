package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;

class MatchState extends SceneState {

    boolean displayControlledPlayer;
    boolean displayBallOwner;
    boolean displayGoalScorer;
    boolean displayTime;
    boolean displayWindVane;
    boolean displayRosters;
    boolean displayScore;
    boolean displayPenaltiesScore;
    boolean displayStatistics;
    boolean displayRadar;
    boolean displayBenchPlayers;
    boolean displayBenchFormation;
    boolean displayTacticsSwitch;

    // convenience references
    protected Match match;
    protected MatchRenderer matchRenderer;

    MatchState(MatchFsm.Id id, MatchFsm matchFsm) {
        super(id, matchFsm);

        this.match = matchFsm.getMatch();
        this.matchRenderer = matchFsm.getRenderer();

        fsm.addState(this);
    }

    MatchFsm getFsm() {
        return (MatchFsm) fsm;
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
