package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.RESTORE_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;

class MatchStatePause extends MatchState {

    private boolean keyPause;
    private boolean waitingNoPauseKey;
    private boolean resume;

    MatchStatePause(MatchFsm fsm) {
        super(STATE_PAUSE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        useHoldStateDisplayFlags();
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);
        waitingNoPauseKey = true;
        resume = false;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // resume on 'P'
        if (waitingNoPauseKey) {
            if (!keyPause) {
                waitingNoPauseKey = false;
            }
        } else if (!Gdx.input.isKeyPressed(Input.Keys.P) && keyPause) {
            resume = true;
        }
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);
    }

    @Override
    void checkConditions() {

        if (resume) {
            fsm.pushAction(RESTORE_FOREGROUND);
            return;
        }

        // resume on fire button
        for (InputDevice d : match.game.inputDevices) {
            if (d.fire1Down()) {
                fsm.pushAction(RESTORE_FOREGROUND);
                return;
            }
        }

        // hand over to replay
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            fsm.pushAction(FADE_OUT);
            fsm.pushAction(NEW_FOREGROUND, STATE_REPLAY);
            fsm.pushAction(FADE_IN);
            return;
        }

        // resume on 'Esc'
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            fsm.pushAction(RESTORE_FOREGROUND);
            return;
        }
    }

    @Override
    void render() {
        super.render();
        Assets.font10.draw(matchRenderer.batch, gettext("PAUSE"), matchRenderer.guiWidth / 2, 22, Font.Align.CENTER);
    }

    private void useHoldStateDisplayFlags() {
        MatchState holdState = match.fsm.getHoldState();

        displayControlledPlayer = holdState.displayControlledPlayer;
        displayBallOwner = holdState.displayBallOwner;
        displayGoalScorer = holdState.displayGoalScorer;
        displayTime = holdState.displayTime;
        displayWindVane = holdState.displayWindVane;
        displayRosters = holdState.displayRosters;
        displayScore = holdState.displayScore;
        displayPenaltiesScore = holdState.displayPenaltiesScore;
        displayStatistics = holdState.displayStatistics;
        displayRadar = holdState.displayRadar;
        displayBenchPlayers = holdState.displayBenchPlayers;
        displayBenchFormation = holdState.displayBenchFormation;
        displayTacticsSwitch = holdState.displayTacticsSwitch;
    }
}
