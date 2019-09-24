package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.Const.BENCH_X;
import static com.ygames.ysoccer.match.Const.BENCH_Y_DOWN;
import static com.ygames.ysoccer.match.Const.BENCH_Y_UP;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_STANDING;

class PlayerStateBenchStanding extends PlayerState {

    PlayerStateBenchStanding(PlayerFsm fsm) {
        super(STATE_BENCH_STANDING, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        if (player.targetDistance() > 1) {
            player.v = 200;
            player.a = player.targetAngle();
        } else {
            player.v = 0;
            player.a = 0;
        }

        player.animationStandRun();
    }

    @Override
    void entryActions() {
        super.entryActions();

        player.tx = BENCH_X + 12;
        if ((1 - 2 * player.team.index) == player.getMatch().benchSide) {
            player.ty = BENCH_Y_UP + 14 * (player.index - TEAM_SIZE) + 46;
        } else {
            player.ty = BENCH_Y_DOWN + 14 * (player.index - TEAM_SIZE) + 46;
        }
    }
}
