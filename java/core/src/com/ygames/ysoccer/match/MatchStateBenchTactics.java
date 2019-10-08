package com.ygames.ysoccer.match;


import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_SUBSTITUTIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateBenchTactics extends MatchState {

    MatchStateBenchTactics(MatchFsm fsm) {
        super(fsm);

        checkReplayKey = false;
        checkPauseKey = false;
    }

    @Override
    void entryActions() {
        super.entryActions();
        displayTacticsSwitch = true;
        getFsm().benchStatus.selectedTactics = getFsm().benchStatus.team.tactics;

        sceneRenderer.actionCamera
                .setTarget(getFsm().benchStatus.targetX, getFsm().benchStatus.targetY)
                .setSpeedMode(FAST);
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

            sceneRenderer.save();

            sceneRenderer.actionCamera.update(REACH_TARGET);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        // change selected tactics
        if (getFsm().benchStatus.inputDevice.yMoved()) {
            getFsm().benchStatus.selectedTactics = EMath.rotate(getFsm().benchStatus.selectedTactics, 0, 17, getFsm().benchStatus.inputDevice.y1);
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        // set selected tactics and go back to bench
        if (getFsm().benchStatus.inputDevice.fire1Down()) {
            if (getFsm().benchStatus.selectedTactics != getFsm().benchStatus.team.tactics) {
                Coach coach = getFsm().benchStatus.team.coach;
                coach.status = Coach.Status.CALL;
                coach.timer = 500;
                getFsm().benchStatus.team.tactics = getFsm().benchStatus.selectedTactics;
            }
            return newAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
        }

        // go back to bench
        if (getFsm().benchStatus.inputDevice.xReleased()) {
            return newAction(NEW_FOREGROUND, STATE_BENCH_SUBSTITUTIONS);
        }

        return checkCommonConditions();
    }
}
