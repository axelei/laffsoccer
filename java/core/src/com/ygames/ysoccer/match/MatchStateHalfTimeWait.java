package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HALF_TIME_ENTER;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHalfTimeWait extends MatchState {

    MatchStateHalfTimeWait(MatchFsm fsm) {
        super(fsm);

        displayTime = true;
        displayWindVane = true;
        displayStatistics = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.swapTeamSides();

        match.kickOffTeam = 1 - match.coinToss;

        sceneRenderer.actionCamera
                .setMode(REACH_TARGET)
                .setSpeed(FAST)
                .setTarget(0, 0);
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
        if (match.team[HOME].fire1Down() != null
                || match.team[AWAY].fire1Down() != null
                || (timer > 3 * SECOND)) {
            match.period = Match.Period.SECOND_HALF;
            return newAction(NEW_FOREGROUND, STATE_HALF_TIME_ENTER);
        }

        return checkCommonConditions();
    }
}
