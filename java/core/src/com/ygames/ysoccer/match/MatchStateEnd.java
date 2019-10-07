package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.badlogic.gdx.Input.Keys.F1;
import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.NORMAL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HIGHLIGHTS;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PAUSE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateEnd extends MatchState {

    MatchStateEnd(MatchFsm fsm) {
        super(fsm);

        displayStatistics = true;

        checkReplayKey = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.period = Match.Period.UNDEFINED;

        sceneRenderer.actionCamera
                .setTarget(0, 0)
                .setSpeedMode(NORMAL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update(REACH_TARGET);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (Gdx.input.isKeyPressed(Input.Keys.H) && match.recorder.hasHighlights()) {
            match.recorder.restart();
            return newFadedAction(NEW_FOREGROUND, STATE_HIGHLIGHTS);
        }

        if (match.team[HOME].fire1Up() != null
                || match.team[AWAY].fire1Up() != null
                || timer > 20 * GLGame.VIRTUAL_REFRESH_RATE) {
            quitMatch();
            return null;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            return newAction(HOLD_FOREGROUND, STATE_PAUSE);
        }

        if (Gdx.input.isKeyPressed(F1)) {
            return newAction(HOLD_FOREGROUND, STATE_HELP);
        }

        return checkCommonConditions();
    }
}
