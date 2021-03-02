package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HIGHLIGHTS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateEnd extends MatchState {

    MatchStateEnd(MatchFsm fsm) {
        super(fsm);

        displayStatistics = true;

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.period = Match.Period.UNDEFINED;

        sceneRenderer.actionCamera
                .setMode(REACH_TARGET)
                .setTarget(0, 0)
                .setSpeed(NORMAL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

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
                || timer > 20 * SECOND) {
            quitMatch();
            return null;
        }

        return checkCommonConditions();
    }
}
