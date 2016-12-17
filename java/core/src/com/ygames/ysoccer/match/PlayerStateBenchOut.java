package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.Const.BENCH_X;

class PlayerStateBenchOut extends PlayerState {

    PlayerStateBenchOut(Player player) {
        super(player);
        id = PlayerFsm.STATE_BENCH_OUT;
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

        player.setTarget(BENCH_X + 16, 12 * (2 * player.team.index - 1) * player.match.benchSide);
    }
}
