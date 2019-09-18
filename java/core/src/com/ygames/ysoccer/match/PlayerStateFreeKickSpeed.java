package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FREE_KICK_SPEED;

class PlayerStateFreeKickSpeed extends PlayerState {

    private float kickAngle;
    private float kickSpin;
    private boolean thrown;

    PlayerStateFreeKickSpeed(PlayerFsm fsm) {
        super(STATE_FREE_KICK_SPEED, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
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
                ball.v = 160 + 160 * f + 300f * timer / Const.SECOND;
                ball.vz = f * (100 + 400f * timer / Const.SECOND);

                boolean longRange = (timer > 0.2f * Const.SECOND);

                if (longRange) {
                    player.searchFarPassingMate();
                } else {
                    player.searchPassingMate();
                }

                if (player.passingMate != null && angleDiff == 0) {
                    ball.a = 45 * player.fmx + player.passingMateAngleCorrection;
                } else {
                    ball.a = 45 * player.fmx;
                }
                ball.s = kickSpin;
            }
        }
    }

    @Override
    PlayerState checkConditions() {
        if (timer > 0.35f * Const.SECOND) {
            return fsm.stateStandRun;
        }
        return null;
    }
}
