package com.ysoccer.android.ysdemo.match;

class PlayerStateKeeperDivingMiddleTwo extends PlayerState {

    private Ball ball;
    private KeeperFrame active;
    private KeeperFrame[] frames = new KeeperFrame[6];

    PlayerStateKeeperDivingMiddleTwo(Player player) {
        super(player);
        id = PlayerFsm.STATE_KEEPER_DIVING_MIDDLE_TWO;

        active = frames[0];

        frames[0] = new KeeperFrame();
        frames[0].fmx = new int[]{4, 5, 6, 7};
        frames[0].fmy = 8;
        frames[0].offx = new int[]{16, -13, 16, -12};
        frames[0].offz = 18;

        frames[1] = new KeeperFrame();
        frames[1].fmx = new int[]{4, 5, 6, 7};
        frames[1].fmy = 8 + 1;
        frames[1].offx = new int[]{16, -13, 16, -12};
        frames[1].offz = 18;

        frames[2] = new KeeperFrame();
        frames[2].fmx = new int[]{4, 5, 6, 7};
        frames[2].fmy = 8 + 2;
        frames[2].offx = new int[]{16, -13, 16, -12};
        frames[2].offz = 18;

        frames[3] = new KeeperFrame();
        frames[3].fmx = new int[]{4, 5, 6, 7};
        frames[3].fmy = 8 + 3;
        frames[3].offx = new int[]{16, -13, 18, -14};
        frames[3].offz = 15;

        frames[4] = new KeeperFrame();
        frames[4].fmx = new int[]{4, 5, 6, 7};
        frames[4].fmy = 8 + 4;
        frames[4].offx = new int[]{13, -10, 17, -13};
        frames[4].offz = 0;

        frames[5] = new KeeperFrame();
        frames[5].fmx = new int[]{4, 5, 6, 7};
        frames[5].fmy = 8 + 5;
        frames[5].offx = new int[]{12, -9, 17, -13};
        frames[5].offz = 0;
    }

    @Override
    void doActions() {
        super.doActions();

        // collision detection
        if (ball.holder != player) {
            player.keeperCollision();
        }

        // animation
        if (timer < 0.05f * Const.SECOND) {
            player.v = 40 * player.thrustX;
            active = frames[0];
        } else if (timer < 0.1f * Const.SECOND) {
            player.v = 40 * player.thrustX;
            player.vz = 70 + 30 * player.thrustZ;
            active = frames[1];
        } else if (timer < 0.5f * Const.SECOND) {
            player.v = (128 + player.skillKeeper) * player.thrustX;
            active = frames[2];
        } else if (timer < 0.7f * Const.SECOND) {
            player.v = 50 * player.thrustX;
            active = frames[3];
        } else if (timer < 0.8f * Const.SECOND) {
            player.v = 20 * player.thrustX;
            active = frames[4];
        } else if (timer < 0.9f * Const.SECOND) {
            player.v = 20 * player.thrustX;
            active = frames[5];
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
        if (timer >= 1.1f * Const.SECOND) {
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
