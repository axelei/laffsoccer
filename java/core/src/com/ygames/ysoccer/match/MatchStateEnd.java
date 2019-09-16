package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.NORMAL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_END;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HIGHLIGHTS;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateEnd extends MatchState {

    MatchStateEnd(MatchFsm fsm) {
        super(STATE_END, fsm);

        displayStatistics = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.period = Match.Period.UNDEFINED;

        matchRenderer.actionCamera.setTarget(0, 0);
        matchRenderer.actionCamera.setSpeedMode(NORMAL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.nextSubframe();

            matchRenderer.save();

            matchRenderer.updateCamera(REACH_TARGET);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (Gdx.input.isKeyPressed(Input.Keys.H) && match.recorder.hasHighlights()) {
            match.recorder.restart();
            fsm.pushAction(FADE_OUT);
            fsm.pushAction(NEW_FOREGROUND, STATE_HIGHLIGHTS);
            fsm.pushAction(FADE_IN);
            return;
        }

        if (match.team[HOME].fire1Up() != null
                || match.team[AWAY].fire1Up() != null
                || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)
                || timer > 20 * GLGame.VIRTUAL_REFRESH_RATE) {
            quitMatch();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(HOLD_FOREGROUND, STATE_PAUSE);
            return;
        }
    }
}
