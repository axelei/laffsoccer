package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.Const.BENCH_X;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BENCH_OUT;

class PlayerStateBenchOut extends PlayerState {

    PlayerStateBenchOut(PlayerFsm fsm) {
        super(STATE_BENCH_OUT, fsm);
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

        player.setTarget(BENCH_X + 16, 12 * (2 * player.team.index - 1) * player.getMatch().benchSide);
    }
}
