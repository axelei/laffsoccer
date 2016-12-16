package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.Const.BENCH_X;
import static com.ygames.ysoccer.match.Const.CENTER_X;
import static com.ygames.ysoccer.match.Const.CENTER_Y;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStateBenchExit extends MatchState {

    MatchStateBenchExit(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_BENCH_EXIT;
    }

    @Override
    void entryActions() {

        Coach coach = match.team[fsm.benchStatus.team.index].coach;

        coach.status = Coach.Status.BENCH;
        coach.x = BENCH_X;

        // reset positions
        for (int i = 0; i < match.settings.benchSize; i++) {
            Player ply = fsm.benchStatus.team.lineup.get(TEAM_SIZE + i);
            if (!ply.fsm.getState().checkId(PlayerFsm.STATE_OUTSIDE)) {
                ply.fsm.setState(PlayerFsm.STATE_BENCH_SITTING);
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

            match.team[HOME].coach.update();
            match.team[AWAY].coach.update();

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, fsm.benchStatus.oldTargetX, false);
            matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_WARP, fsm.benchStatus.oldTargetY);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {

        float dx = matchRenderer.actionCamera.x - fsm.benchStatus.oldTargetX - CENTER_X + matchRenderer.screenWidth / (2 * matchRenderer.zoom / 100f);
        float dy = matchRenderer.actionCamera.y - fsm.benchStatus.oldTargetY - CENTER_Y + matchRenderer.screenHeight / (2 * matchRenderer.zoom / 100f);

        if (Emath.hypo(dx, dy) <= 1) {
            fsm.pushAction(MatchFsm.ActionType.RESTORE_FOREGROUND);
            return;
        }
    }
}
