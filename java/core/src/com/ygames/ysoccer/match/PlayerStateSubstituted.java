package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.Const.BENCH_X;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_SUBSTITUTED;

class PlayerStateSubstituted extends PlayerState {

    private float v, tx, ty;

    PlayerStateSubstituted(PlayerFsm fsm) {
        super(STATE_SUBSTITUTED, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        tx = BENCH_X + 16;
        ty = 12 * (2 * player.team.index - 1) * player.getMatch().benchSide;

        player.isActive = false;

        v = 180 + EMath.rand(0, 30);
    }

    @Override
    void doActions() {
        super.doActions();
        if (player.isVisible && player.distanceFrom(tx, ty) > 1.5f) {
            player.v = v;
            player.a = player.angleToPoint(tx, ty);
        } else {
            player.v = 0;
            player.isVisible = false;
        }

        player.animationStandRun();
    }

    @Override
    void exitActions() {
        super.exitActions();
        player.isVisible = true;
    }
}
