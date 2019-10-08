package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.WARP;
import static com.ygames.ysoccer.match.Const.CENTER_X;
import static com.ygames.ysoccer.match.Const.CENTER_Y;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_SITTING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;
import static java.lang.Math.min;

class MatchStateBenchExit extends MatchState {

    MatchStateBenchExit(MatchFsm fsm) {
        super(fsm);

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Coach coach = getFsm().benchStatus.team.coach;
        coach.status = Coach.Status.BENCH;

        // reset positions
        int substitutes = min(match.getSettings().benchSize, getFsm().benchStatus.team.lineup.size() - TEAM_SIZE);
        for (int i = 0; i < substitutes; i++) {
            Player player = getFsm().benchStatus.team.lineup.get(TEAM_SIZE + i);
            if (!player.getState().checkId(STATE_OUTSIDE)) {
                player.setState(STATE_BENCH_SITTING);
            }
        }

        sceneRenderer.actionCamera
                .setTarget(getFsm().benchStatus.oldTargetX, getFsm().benchStatus.oldTargetY)
                .setSpeedMode(WARP);
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
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        float dx = sceneRenderer.actionCamera.x - getFsm().benchStatus.oldTargetX - CENTER_X + sceneRenderer.screenWidth / (2 * sceneRenderer.zoom / 100f);
        float dy = sceneRenderer.actionCamera.y - getFsm().benchStatus.oldTargetY - CENTER_Y + sceneRenderer.screenHeight / (2 * sceneRenderer.zoom / 100f);

        // TODO: replace with test on speed provided by camera
        if (EMath.hypo(dx, dy) <= 1) {
            return newAction(RESTORE_FOREGROUND);
        }

        return checkCommonConditions();
    }
}
