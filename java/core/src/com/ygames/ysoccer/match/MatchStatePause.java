package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.MatchFsm.ActionType.RESTORE_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;

class MatchStatePause extends MatchState {

    private boolean keyPause;
    private boolean waitingNoPauseKey;
    private boolean resume;

    MatchStatePause(MatchFsm fsm) {
        super(STATE_PAUSE, fsm);

        displayWindVane = true;
    }

    @Override
    void entryActions() {
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

        // resume on 'Esc'
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            fsm.pushAction(RESTORE_FOREGROUND);
            return;
        }
    }

    @Override
    void render() {
        super.render();
        matchRenderer.batch.draw(Assets.pause, 34, 28);
    }
}
