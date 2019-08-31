package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_SPEED;

class PlayerStatePenaltyKickSpeed extends PlayerState {

    private float kickAngle;
    private float kickSpin;
    private boolean endOfAfterEffect;

    PlayerStatePenaltyKickSpeed(PlayerFsm fsm, Player player) {
        super(STATE_PENALTY_KICK_SPEED, fsm, player);
    }

    @Override
    void entryActions() {
        super.entryActions();
        kickAngle = player.a;
        kickSpin = 0;
        endOfAfterEffect = false;
    }

    @Override
    void doActions() {
        super.doActions();

        if (!endOfAfterEffect) {

            if (timer > 0.15f * Const.SECOND && !player.inputDevice.fire11) {
                endOfAfterEffect = true;
            }
            if (timer > 0.3f * Const.SECOND) {
                endOfAfterEffect = true;
            }

            float angleDiff;
            float powerFactor;
            if (player.inputDevice.value) {
                angleDiff = ((player.inputDevice.angle - kickAngle + 540) % 360) - 180;
                if (Math.abs(angleDiff) < 67.5f) {
                    powerFactor = 0;
                } else if (Math.abs(angleDiff) < 112.5f) {
                    powerFactor = 0.6f;
                } else {
                    powerFactor = 1.0f;
                }

                // spin
                if ((Math.abs(angleDiff) > 22.5f) && (Math.abs(angleDiff) < 157.5f)) {
                    kickSpin += 50.0f / Const.SECOND * Emath.sgn(angleDiff);
                }
            } else {
                powerFactor = 0.6f;
            }

            if (endOfAfterEffect) {
                ball.v = 160 + 120 * powerFactor + 300f * timer / Const.SECOND;
                ball.vz = powerFactor * (100 + 350f * timer / Const.SECOND);

                ball.a = player.a;
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
