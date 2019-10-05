package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_SPEED;

class PlayerStatePenaltyKickSpeed extends PlayerState {

    private float kickAngle;
    private float kickSpin;
    private boolean endOfAfterEffect;

    PlayerStatePenaltyKickSpeed(PlayerFsm fsm) {
        super(STATE_PENALTY_KICK_SPEED, fsm);
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

            if (timer > 0.15f * SECOND && !player.inputDevice.fire11) {
                endOfAfterEffect = true;
            }
            if (timer > 0.3f * SECOND) {
                endOfAfterEffect = true;
            }

            float factor = 0.8f; // medium shots
            if (player.inputDevice.value) {
                float angleDiff = ((player.inputDevice.angle - kickAngle + 540) % 360) - 180;
                // low shots
                if (Math.abs(angleDiff) < 67.5f) {
                    factor = 0.35f;
                }
                // high shots
                else if (Math.abs(angleDiff) > 112.5f) {
                    factor = 1.0f;
                }

                // spin
                if ((Math.abs(angleDiff) > 22.5f) && (Math.abs(angleDiff) < 157.5f)) {
                    kickSpin += 50.0f / SECOND * EMath.sgn(angleDiff);
                }
            }

            if (endOfAfterEffect) {
                ball.v = 160 + 120 * factor + 300f * timer / SECOND;
                ball.vz = factor * (100 + 350f * timer / SECOND);

                ball.a = player.a;
                ball.s = kickSpin;
            }
        }
    }

    @Override
    PlayerState checkConditions() {
        if (timer > 0.35f * SECOND) {
            return fsm.stateStandRun;
        }
        return null;
    }
}
