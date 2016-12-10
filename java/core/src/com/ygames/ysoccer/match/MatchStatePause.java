package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.InputDevice;

class MatchStatePause extends MatchState {

    private boolean keyPause;
    private boolean waitingNoPauseKey;
    private boolean resume;

    MatchStatePause(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_PAUSE;
    }

    @Override
    void entryActions() {
        matchRenderer.displayControlledPlayer = false;
        matchRenderer.displayTime = false;
        matchRenderer.displayRadar = false;

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
            match.fsm.pushAction(MatchFsm.ActionType.RESTORE_FOREGROUND);
            return;
        }

        // resume on fire button
        for (InputDevice d : match.game.inputDevices) {
            if (d.fire1Down()) {
                match.fsm.pushAction(MatchFsm.ActionType.RESTORE_FOREGROUND);
                return;
            }
        }

        // resume on 'Esc'
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            match.fsm.pushAction(MatchFsm.ActionType.RESTORE_FOREGROUND);
            return;
        }
    }

    @Override
    void render() {
        super.render();
        matchRenderer.batch.draw(Assets.pause, 34, 28);
    }
}
