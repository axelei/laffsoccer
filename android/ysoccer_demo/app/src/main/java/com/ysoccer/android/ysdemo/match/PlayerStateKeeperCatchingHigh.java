package com.ysoccer.android.ysdemo.match;

class PlayerStateKeeperCatchingHigh extends PlayerState {

    private Ball ball;
    private KeeperFrame active;
    private KeeperFrame[] frames = new KeeperFrame[4];

    PlayerStateKeeperCatchingHigh(Player player) {
        super(player);
        id = PlayerFsm.STATE_KEEPER_CATCHING_HIGH;

        active = frames[0];

        frames[0] = new KeeperFrame();
        frames[0].fmx = new int[]{0, 0, 1, 1};
        frames[0].fmy = 8;
        frames[0].offx = new int[]{2, 0, 0, 0};
        frames[0].offz = 30;

        frames[1] = new KeeperFrame();
        frames[1].fmx = new int[]{0, 0, 1, 1};
        frames[1].fmy = 8 + 9;
        frames[1].offx = new int[]{2, 0, 0, 0};
        frames[1].offz = 30;

        frames[2] = new KeeperFrame();
        frames[2].fmx = new int[]{0, 0, 1, 1};
        frames[2].fmy = 8 + 10;
        frames[2].offx = new int[]{2, 0, 0, 0};
        frames[2].offz = 30;

        frames[3] = new KeeperFrame();
        frames[3].fmx = new int[]{0, 0, 1, 1};
        frames[3].fmy = 8 + 8;
        frames[3].offx = new int[]{2, 0, 0, 0};
        frames[3].offz = 15;
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
            player.v = 0;
            active = frames[0];
        } else if (timer < 0.2f * Const.SECOND) {
            player.vz = 35;
            active = frames[1];
        } else if (timer < 0.6f * Const.SECOND) {
            player.vz = 30;
            active = frames[2];
        } else if (timer < 0.9f * Const.SECOND) {
            if (ball.owner != player) {
                active = frames[3];
            }
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
