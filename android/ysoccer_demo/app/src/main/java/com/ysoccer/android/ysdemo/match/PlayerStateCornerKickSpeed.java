package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

class PlayerStateCornerKickSpeed extends PlayerState {

    private Ball ball;
    private float kickAngle;
    private float kickSpin;
    private boolean thrown;

    PlayerStateCornerKickSpeed(Player player) {
        super(player);
        id = PlayerFsm.STATE_CORNER_KICK_SPEED;
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball = player.match.ball;
        kickAngle = 45 * player.fmx;
        kickSpin = 0;
        thrown = false;
    }

    @Override
    void doActions() {
        super.doActions();

        if (!thrown) {

            if (timer > 0.15f * Const.SECOND && !player.inputDevice.fire11) {
                thrown = true;
            }
            if (timer > 0.3f * Const.SECOND) {
                thrown = true;
            }

            float angleDiff;
            if (player.inputDevice.value) {
                angleDiff = ((player.inputDevice.angle - kickAngle + 540) % 360) - 180;
            } else {
                angleDiff = 90;
            }

            // spin
            if (player.inputDevice.value) {
                if ((Math.abs(angleDiff) > 22.5f) && (Math.abs(angleDiff) < 157.5f)) {
                    kickSpin += 50.0f / Const.SECOND * Emath.sgn(angleDiff);
                }
            }

            if (thrown) {

                float f;
                if (Math.abs(angleDiff) < 67.5f) {
                    f = 0;
                } else if (Math.abs(angleDiff) < 112.5f) {
                    f = 1.0f;
                } else {
                    f = 1.3f;
                }
                ball.v = 160 + 160 * f + 300 * timer / Const.SECOND;
                ball.vz = f * (100 + 400 * timer / Const.SECOND);

                boolean longRange = (timer > 0.2f * Const.SECOND);

                if (player.searchFacingPlayer(longRange) && angleDiff == 0) {
                    ball.a = 45 * player.fmx + player.facingAngle;
                } else {
                    switch (Math.round(player.fmx)) {
                        case 0:
                            ball.a = 0 - 5 * ball.ySide;
                            break;
                        case 2:
                            ball.a = 90 + 5 * ball.xSide;
                            break;
                        case 4:
                            ball.a = 180 + 5 * ball.ySide;
                            break;
                        case 6:
                            ball.a = 270 - 5 * ball.xSide;
                            break;
                        default:
                            ball.a = 45 * player.fmx;
                            break;
                    }
                }
                ball.s = kickSpin;
            }
        }
    }

    @Override
    PlayerState checkConditions() {
        if (timer > 0.35f * Const.SECOND) {
            return player.fsm.stateStandRun;
        }
        return null;
    }
}
