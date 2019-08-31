package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_CATCHING_LOW;

class PlayerStateKeeperCatchingLow extends PlayerState {

    private KeeperFrame active;
    private KeeperFrame[] frames = new KeeperFrame[1];

    PlayerStateKeeperCatchingLow(PlayerFsm fsm) {
        super(STATE_KEEPER_CATCHING_LOW, fsm);

        active = frames[0];

        frames[0] = new KeeperFrame();
        frames[0].fmx = new int[]{2, 2, 6, 6};
        frames[0].fmy = 1;
        frames[0].offx = new int[]{0, 0, 0, 0};
        frames[0].offz = 10;
    }

    @Override
    void doActions() {
        super.doActions();

        // collision detection
        if (ball.holder != player) {
            player.keeperCollision();
        }

        // animation
        player.v = 0;
        active = frames[0];

        int p = ((player.a == 180) ? 1 : 0) + ((player.y > 0) ? 2 : 0);
        player.fmx = active.fmx[p];
        player.fmy = active.fmy;
        player.holdBall(active.offx[p], active.offz);
    }

    @Override
    State checkConditions() {
        if (timer >= 1.0f * Const.SECOND) {
            if (ball.holder == player) {
                 return fsm.stateKeeperKickAngle;
            } else {
                return fsm.stateKeeperPositioning;
            }
        }
        return null;
    }

    @Override
    void exitActions() {
        ball.setHolder(null);
    }
}
