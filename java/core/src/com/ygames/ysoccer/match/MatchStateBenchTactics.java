package com.ygames.ysoccer.match;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.ActionCamera.CF_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_TACTICS;

class MatchStateBenchTactics extends MatchState {

    MatchStateBenchTactics(MatchFsm fsm) {
        super(STATE_BENCH_TACTICS, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        displayTacticsSwitch = true;
        fsm.benchStatus.selectedTactics = fsm.benchStatus.team.tactics;
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

            match.save();

            matchRenderer.updateCameraX(CF_TARGET, FAST, fsm.benchStatus.targetX);
            matchRenderer.updateCameraY(CF_TARGET, FAST, fsm.benchStatus.targetY);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        // change selected tactics
        if (fsm.benchStatus.inputDevice.yMoved()) {
            fsm.benchStatus.selectedTactics = Emath.rotate(fsm.benchStatus.selectedTactics, 0, 17, fsm.benchStatus.inputDevice.y1);
        }
    }

    @Override
    void checkConditions() {

        // set selected tactics and go back to bench
        if (fsm.benchStatus.inputDevice.fire1Down()) {
            if (fsm.benchStatus.selectedTactics != fsm.benchStatus.team.tactics) {
                Coach coach = fsm.benchStatus.team.coach;
                coach.status = Coach.Status.CALL;
                coach.timer = 500;
                fsm.benchStatus.team.tactics = fsm.benchStatus.selectedTactics;
            }
            fsm.pushAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
            return;
        }

        // go back to bench
        if (fsm.benchStatus.inputDevice.xReleased() || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            fsm.pushAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
        return;
    }
}
