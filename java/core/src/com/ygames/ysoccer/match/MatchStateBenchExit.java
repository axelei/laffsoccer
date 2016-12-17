package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.ActionCamera.CF_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.CS_FAST;
import static com.ygames.ysoccer.match.ActionCamera.CS_WARP;
import static com.ygames.ysoccer.match.Const.CENTER_X;
import static com.ygames.ysoccer.match.Const.CENTER_Y;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.RESTORE_FOREGROUND;
import static com.ygames.ysoccer.match.PlayerFsm.STATE_BENCH_SITTING;
import static com.ygames.ysoccer.match.PlayerFsm.STATE_OUTSIDE;

class MatchStateBenchExit extends MatchState {

    MatchStateBenchExit(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_BENCH_EXIT;
    }

    @Override
    void entryActions() {

        Coach coach = fsm.benchStatus.team.coach;
        coach.status = Coach.Status.BENCH;

        // reset positions
        for (int i = 0; i < match.settings.benchSize; i++) {
            Player player = fsm.benchStatus.team.lineup.get(TEAM_SIZE + i);
            if (!player.fsm.getState().checkId(STATE_OUTSIDE)) {
                player.fsm.setState(STATE_BENCH_SITTING);
            }
        }
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

            matchRenderer.updateCameraX(CF_TARGET, CS_FAST, fsm.benchStatus.oldTargetX, false);
            matchRenderer.updateCameraY(CF_TARGET, CS_WARP, fsm.benchStatus.oldTargetY);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {

        float dx = matchRenderer.actionCamera.x - fsm.benchStatus.oldTargetX - CENTER_X + matchRenderer.screenWidth / (2 * matchRenderer.zoom / 100f);
        float dy = matchRenderer.actionCamera.y - fsm.benchStatus.oldTargetY - CENTER_Y + matchRenderer.screenHeight / (2 * matchRenderer.zoom / 100f);

        if (Emath.hypo(dx, dy) <= 1) {
            fsm.pushAction(RESTORE_FOREGROUND);
            return;
        }
    }
}
