package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.math.Emath;

class PlayerStateThrowInSpeed extends PlayerState {

    Ball ball;
    private boolean thrown;

    PlayerStateThrowInSpeed(Player player) {
        super(player);
        id = PlayerFsm.STATE_THROW_IN_SPEED;
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball = player.match.ball;
        thrown = false;
    }

    @Override
    void doActions() {
        super.doActions();

        if (!thrown) {
            ball.x = player.x;
            ball.y = player.y;
            ball.z = Const.PLAYER_H + 2;
            player.fmy = 8;

            if ((timer > 0.15f * GlGame.SUBFRAMES_PER_SECOND) && (!player.inputDevice.fire11)) {
                thrown = true;
            }
            if (timer > 0.3f * GlGame.SUBFRAMES_PER_SECOND) {
                thrown = true;
            }

            if (thrown) {
                ball.x = player.x + 6 * Emath.cos(player.a);
                ball.y = player.y + 6 * Emath.sin(player.a);
                ball.v = 30 + 1000 * timer / GlGame.SUBFRAMES_PER_SECOND;
                ball.vz = 500 * timer / GlGame.SUBFRAMES_PER_SECOND;
                switch (Math.round(player.fmx)) {
                    case 2:
                        ball.a = 90 + (10 + 5 * player.match.settings.wind.speed) * ball.xSide;
                        break;

                    case 6:
                        ball.a = 270 - (10 + 5 * player.match.settings.wind.speed) * ball.xSide;
                        break;

                    default:
                        ball.a = 45 * player.fmx;
                        break;
                }
                if (player.searchFacingPlayer(timer > 0.3f * GlGame.SUBFRAMES_PER_SECOND)) {
                    ball.a += player.facingAngle;
                }

                player.fmy = 9;
            }
        }
    }

    @Override
    State checkConditions() {
        if (timer > 0.35f * GlGame.SUBFRAMES_PER_SECOND) {
            return player.fsm.stateStandRun;
        }
        return null;
    }
}