package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStateHalfTimeWait extends MatchState {

    MatchStateHalfTimeWait(MatchFsm fsm) {
        super(MatchFsm.STATE_HALF_TIME_WAIT, fsm);

        displayTime = true;
        displayWindVane = true;
        displayStatistics = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.swapTeamSides();

        match.kickOffTeam = 1 - match.coinToss;
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
        if (match.team[HOME].fire1Down() != null
                || match.team[AWAY].fire1Down() != null
                || (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE)) {
            match.period = Match.Period.SECOND_HALF;
            fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_HALF_TIME_ENTER);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_PAUSE);
            return;
        }
    }
}
