package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_SPEED;

class PlayerStateThrowInSpeed extends PlayerState {

    private boolean thrown;

    PlayerStateThrowInSpeed(PlayerFsm fsm) {
        super(STATE_THROW_IN_SPEED, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        thrown = false;
    }

    @Override
    void doActions() {
        super.doActions();

        if (!thrown) {
            ball.setX(player.x);
            ball.setY(player.y);
            ball.setZ(Const.PLAYER_H + 2);
            player.fmy = 8;

            if ((timer > 0.15f * GLGame.SUBFRAMES_PER_SECOND) && (!player.inputDevice.fire11)) {
                thrown = true;
            }
            if (timer > 0.3f * GLGame.SUBFRAMES_PER_SECOND) {
                thrown = true;
            }

            if (thrown) {
                ball.setX(player.x + 6 * EMath.cos(player.a));
                ball.setY(player.y + 6 * EMath.sin(player.a));
                ball.v = 30 + 1000f * timer / GLGame.SUBFRAMES_PER_SECOND;
                ball.vz = 500f * timer / GLGame.SUBFRAMES_PER_SECOND;
                switch (Math.round(player.fmx)) {
                    case 2:
                        ball.a = 90 + (10 + 5 * player.scene.settings.wind.speed) * ball.xSide;
                        break;

                    case 6:
                        ball.a = 270 - (10 + 5 * player.scene.settings.wind.speed) * ball.xSide;
                        break;

                    default:
                        ball.a = 45 * player.fmx;
                        break;
                }

                boolean longRange = timer > 0.3f * GLGame.SUBFRAMES_PER_SECOND;

                if (longRange) {
                    player.searchFarPassingMate();
                } else {
                    player.searchPassingMate();
                }

                if (player.passingMate != null) {
                    ball.a += player.passingMateAngleCorrection;
                }

                player.fmy = 9;
            }
        }
    }

    @Override
    State checkConditions() {
        if (timer > 0.35f * GLGame.SUBFRAMES_PER_SECOND) {
            return fsm.stateStandRun;
        }
        return null;
    }
}
