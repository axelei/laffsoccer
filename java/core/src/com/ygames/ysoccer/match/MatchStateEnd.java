package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStateEnd extends MatchState {

    MatchStateEnd(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_END;

        displayStatistics = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.period = Match.Period.UNDEFINED;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);
            matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (match.team[HOME].fire1Up() != null
                || match.team[AWAY].fire1Up() != null
                || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)
                || timer > 20 * GLGame.VIRTUAL_REFRESH_RATE) {
            quitMatch();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_PAUSE);
            return;
        }
    }
}
