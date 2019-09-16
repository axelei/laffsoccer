package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.WARP;
import static com.ygames.ysoccer.match.Const.CENTER_X;
import static com.ygames.ysoccer.match.Const.CENTER_Y;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_EXIT;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_SITTING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;
import static java.lang.Math.min;

class MatchStateBenchExit extends MatchState {

    MatchStateBenchExit(MatchFsm fsm) {
        super(STATE_BENCH_EXIT, fsm);
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

        matchRenderer.actionCamera.setTarget(getFsm().benchStatus.oldTargetX, getFsm().benchStatus.oldTargetY);
        matchRenderer.actionCamera.setSpeedMode(WARP);
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
    }

    @Override
    void checkConditions() {

        float dx = matchRenderer.actionCamera.x - getFsm().benchStatus.oldTargetX - CENTER_X + matchRenderer.screenWidth / (2 * matchRenderer.zoom / 100f);
        float dy = matchRenderer.actionCamera.y - getFsm().benchStatus.oldTargetY - CENTER_Y + matchRenderer.screenHeight / (2 * matchRenderer.zoom / 100f);

        // TODO: replace with test on speed provided by camera
        if (Emath.hypo(dx, dy) <= 1) {
            fsm.pushAction(RESTORE_FOREGROUND);
            return;
        }
    }
}
