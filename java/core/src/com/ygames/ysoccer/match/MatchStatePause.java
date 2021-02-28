package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.match.MatchFsm.STATE_REPLAY;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;

class MatchStatePause extends MatchState {

    private boolean keyPause;
    private boolean waitingNoPauseKey;
    private boolean resume;

    MatchStatePause(MatchFsm fsm) {
        super(fsm);

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
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
    SceneFsm.Action[] checkConditions() {

        if (resume) {
            return newAction(RESTORE_FOREGROUND);
        }

        // resume on fire button
        for (InputDevice d : match.game.inputDevices) {
            if (d.fire1Down()) {
                return newAction(RESTORE_FOREGROUND);
            }
        }

        // hand over to replay
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            return newFadedAction(NEW_FOREGROUND, STATE_REPLAY);
        }

        return checkCommonConditions();
    }

    @Override
    void render() {
        super.render();
        Assets.font10.draw(sceneRenderer.batch, gettext("PAUSE"), sceneRenderer.guiWidth / 2, 22, Font.Align.CENTER);
    }

    private void useHoldStateDisplayFlags() {
        MatchState holdState = match.getFsm().getHoldState();

        displayControlledPlayer = holdState.displayControlledPlayer;
        displayBallOwner = holdState.displayBallOwner;
        displayFoulMaker = holdState.displayFoulMaker;
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
