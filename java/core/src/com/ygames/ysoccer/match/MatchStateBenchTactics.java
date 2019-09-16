package com.ygames.ysoccer.match;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_TACTICS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateBenchTactics extends MatchState {

    MatchStateBenchTactics(MatchFsm fsm) {
        super(STATE_BENCH_TACTICS, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        displayTacticsSwitch = true;
        getFsm().benchStatus.selectedTactics = getFsm().benchStatus.team.tactics;

        matchRenderer.actionCamera.setTarget(getFsm().benchStatus.targetX, getFsm().benchStatus.targetY);
        matchRenderer.actionCamera.setSpeedMode(FAST);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.updateBall();
            match.ball.inFieldKeep();

            match.updatePlayers(true);

            match.updateCoaches();

            match.nextSubframe();

            matchRenderer.save();

            matchRenderer.updateCamera(REACH_TARGET);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        // change selected tactics
        if (getFsm().benchStatus.inputDevice.yMoved()) {
            getFsm().benchStatus.selectedTactics = Emath.rotate(getFsm().benchStatus.selectedTactics, 0, 17, getFsm().benchStatus.inputDevice.y1);
        }
    }

    @Override
    void checkConditions() {

        // set selected tactics and go back to bench
        if (getFsm().benchStatus.inputDevice.fire1Down()) {
            if (getFsm().benchStatus.selectedTactics != getFsm().benchStatus.team.tactics) {
                Coach coach = getFsm().benchStatus.team.coach;
                coach.status = Coach.Status.CALL;
                coach.timer = 500;
                getFsm().benchStatus.team.tactics = getFsm().benchStatus.selectedTactics;
            }
            fsm.pushAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
            return;
        }

        // go back to bench
        if (getFsm().benchStatus.inputDevice.xReleased() || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            fsm.pushAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
        return;
    }
}
