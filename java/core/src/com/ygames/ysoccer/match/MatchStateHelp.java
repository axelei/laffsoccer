package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;

class MatchStateHelp extends MatchState {

    private boolean keyHelp;
    private boolean waitingNoHelpKey;
    private boolean resume;

    MatchStateHelp(MatchFsm fsm) {
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
        keyHelp = Gdx.input.isKeyPressed(Input.Keys.F1);
        waitingNoHelpKey = true;
        resume = false;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // resume on 'F1'
        if (waitingNoHelpKey) {
            if (!keyHelp) {
                waitingNoHelpKey = false;
            }
        } else if (!Gdx.input.isKeyPressed(Input.Keys.F1) && keyHelp) {
            resume = true;
        }
        keyHelp = Gdx.input.isKeyPressed(Input.Keys.F1);
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

        return checkCommonConditions();
    }

    @Override
    void render() {
        super.render();
        sceneRenderer.drawHelp(match.getFsm().getHotKeys().keyMap);
    }

    private void useHoldStateDisplayFlags() {
        MatchState holdState = match.getFsm().getHoldState();

        displayTime = holdState.displayTime;
        displayWindVane = holdState.displayWindVane;
    }
}
