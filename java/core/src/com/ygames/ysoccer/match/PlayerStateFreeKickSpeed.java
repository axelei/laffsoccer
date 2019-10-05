package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

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
                    kickSpin += 50.0f / Const.SECOND * EMath.sgn(angleDiff);
                }
            }

            if (thrown) {

                float factor;

                // low shots
                if (Math.abs(angleDiff) < 67.5f) {
                    factor = 0.35f;
                }
                // medium shots
                else if (Math.abs(angleDiff) < 112.5f) {
                    factor = 1.0f;
                }
                // high shots
                else {
                    factor = 1.3f;
                }

                // 240 + 7 + 37 : 96 = 284 : 343 (low)
                // 240 + 20 + 37 : 96 = 297 : 356 (medium)
                // 240 + 26 + 37 : 96 = 303 : 362 (high)
                ball.v = 240 + 20 * factor + (250f + 10 * player.skills.shooting) * timer / Const.SECOND;
                ball.vz = factor * (100 + 400f * timer / Const.SECOND);

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
