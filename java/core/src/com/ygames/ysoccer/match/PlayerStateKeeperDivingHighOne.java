package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_DIVING_HIGH_ONE;

class PlayerStateKeeperDivingHighOne extends PlayerState {

    private KeeperFrame active;
    private KeeperFrame[] frames = new KeeperFrame[5];

    PlayerStateKeeperDivingHighOne(PlayerFsm fsm) {
        super(STATE_KEEPER_DIVING_HIGH_ONE, fsm);

        active = frames[0];

        frames[0] = new KeeperFrame();
        frames[0].fmx = new int[]{4, 5, 6, 7};
        frames[0].fmy = 8;
        frames[0].offx = new int[]{16, -13, 16, -12};
        frames[0].offz = 18;

        frames[1] = new KeeperFrame();
        frames[1].fmx = new int[]{0, 1, 2, 3};
        frames[1].fmy = 8 + 5;
        frames[1].offx = new int[]{13, -11, 10, -10};
        frames[1].offz = 26;

        frames[2] = new KeeperFrame();
        frames[2].fmx = new int[]{0, 1, 2, 3};
        frames[2].fmy = 8 + 6;
        frames[2].offx = new int[]{13, -11, 10, -10};
        frames[2].offz = 26;

        frames[3] = new KeeperFrame();
        frames[3].fmx = new int[]{0, 1, 2, 3};
        frames[3].fmy = 8 + 7;
        frames[3].offx = new int[]{14, -12, 10, -10};
        frames[3].offz = 20;

        frames[4] = new KeeperFrame();
        frames[4].fmx = new int[]{0, 1, 2, 3};
        frames[4].fmy = 8 + 4;
        frames[4].offx = new int[]{8, -5, 15, -11};
        frames[4].offz = 0;
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
            player.v = 10;
            active = frames[0];
        } else if (timer < 0.2f * Const.SECOND) {
            player.v = 40 * player.thrustX;
            active = frames[1];
        } else if (timer < 0.7f * Const.SECOND) {
            player.v = (140 + 0.5f * player.getSkillKeeper()) * player.thrustX;
            player.vz = 15 + 0.5f * player.getSkillKeeper() + 10 * player.thrustZ;
            active = frames[2];
        } else if (timer < 1.1f * Const.SECOND) {
            player.v = (50 + 0.5f * player.getSkillKeeper()) * player.thrustX;
            active = frames[3];
        } else if (timer < 1.35f * Const.SECOND) {
            player.v = player.v * (1 - 50.0f / Const.SECOND);
            active = frames[4];
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
        if (timer >= 1.55f * Const.SECOND) {
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
