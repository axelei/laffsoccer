package com.ysoccer.android.ysdemo.match;

class PlayerStateKeeperDivingLowSingle extends PlayerState {

    private Ball ball;
    private KeeperFrame active;
    private KeeperFrame[] frames = new KeeperFrame[3];

    PlayerStateKeeperDivingLowSingle(Player player) {
        super(player);
        id = PlayerFsm.STATE_KEEPER_DIVING_LOW_SINGLE;

        active = frames[0];

        frames[0] = new KeeperFrame();
        frames[0].fmx = new int[]{0, 1, 2, 3};
        frames[0].fmy = 8;
        frames[0].offx = new int[]{13, -10, 17, -13};
        frames[0].offz = 0;

        frames[1] = new KeeperFrame();
        frames[1].fmx = new int[]{4, 5, 6, 7};
        frames[1].fmy = 8 + 4;
        frames[1].offx = new int[]{13, -10, 17, -13};
        frames[1].offz = 0;

        frames[2] = new KeeperFrame();
        frames[2].fmx = new int[]{4, 5, 6, 7};
        frames[2].fmy = 8 + 6;
        frames[2].offx = new int[]{16, -15, 14, -10};
        frames[2].offz = 0;
    }

    @Override
    void doActions() {
        super.doActions();

        // collision detection
        if (ball.holder != player) {
            player.keeperCollision();
        }

        // animation
        if (timer < 0.05 * Const.SECOND) {
            player.v = 120;
            active = frames[0];
        } else if (timer < 0.4f * Const.SECOND) {
            player.v = 100 + 20 * player.thrustX;
            active = frames[1];
        } else if (timer < 0.6f * Const.SECOND) {
            player.v = 100 + 20 * player.thrustX;
            active = frames[2];
        } else {
            player.v = player.v * (1 - 50.0f / Const.SECOND);
        }

        int p = ((player.a == 180) ? 1 : 0) + ((player.y > 0) ? 2 : 0);
        player.fmx = active.fmx[p];
        player.fmy = active.fmy;
        player.holdBall(active.offx[p], active.offz);

    }

    @Override
    State checkConditions() {
        if (timer >= 1.0f * Const.SECOND) {
            if (ball.holder == player) {
                return player.fsm.stateKeeperKickAngle;
            } else {
                return player.fsm.stateKeeperPositioning;
            }
        }
        return null;
    }

    @Override
    void exitActions() {
        ball.setHolder(null);
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball = player.match.ball;
    }
}
