package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.InputDevice;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.F1;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_REPLAY;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;

abstract class MatchState extends SceneState {

    boolean displayControlledPlayer;
    boolean displayBallOwner;
    boolean displayFoulMaker;
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

    boolean checkReplayKey = true;
    boolean checkPauseKey = true;
    boolean checkHelpKey = true;
    boolean checkBenchCall = true;

    final Match match;
    final Ball ball;

    MatchState(MatchFsm matchFsm) {
        super(matchFsm);

        this.match = matchFsm.getMatch();
        this.ball = match.ball;
    }

    MatchFsm getFsm() {
        return (MatchFsm) fsm;
    }

    SceneFsm.Action[] checkCommonConditions() {

        if (checkReplayKey && Gdx.input.isKeyPressed(R)) {
            return newFadedAction(HOLD_FOREGROUND, STATE_REPLAY);
        }

        if (checkPauseKey && Gdx.input.isKeyPressed(P)) {
            return newAction(HOLD_FOREGROUND, STATE_PAUSE);
        }

        if (checkHelpKey && Gdx.input.isKeyPressed(F1)) {
            return newAction(HOLD_FOREGROUND, STATE_HELP);
        }

        if (checkBenchCall) {
            for (int t = HOME; t <= AWAY; t++) {
                InputDevice inputDevice = match.team[t].fire2Down();
                if (inputDevice != null) {
                    getFsm().benchStatus.team = match.team[t];
                    getFsm().benchStatus.inputDevice = inputDevice;
                    return newAction(HOLD_FOREGROUND, STATE_BENCH_ENTER);
                }
            }
        }

        if (Gdx.input.isKeyPressed(ESCAPE)) {
            quitMatch();
            return null;
        }

        return null;
    }

    void quitMatch() {
        match.quit();
    }
}
