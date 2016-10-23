package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

public class PlayerStateKick extends PlayerState {

    private Ball ball;
    private int isPassing;

    private static int IP_UNKNOWN = -1;
    private static int IP_FALSE = 0;
    private static int IP_TRUE = 1;

    public PlayerStateKick(Player player) {
        super(player);
        id = PlayerFsm.STATE_KICK;
    }

    @Override
    void doActions() {
        super.doActions();

        float angle;
        if (player.inputDevice.value) {
            angle = player.inputDevice.angle;
        } else {
            angle = player.kickAngle;
        }
        float angle_diff = ((angle - player.kickAngle + 540.0f) % 360.0f) - 180.0f;

        //detect passing
        if (timer > 0.1 * Const.SECOND && isPassing == IP_UNKNOWN) {
            if (player.inputDevice.fire11) {
                isPassing = IP_FALSE;
            } else {
                isPassing = IP_TRUE;

                //automatic angle correction
                if ((angle_diff == 0) && player.searchFacingPlayer(false)) {
                    ball.a += player.facingAngle;
                    float d = Emath.dist(player.facingPlayer.x, player.facingPlayer.y, player.x, player.y);
                    ball.v += (0.035f + 0.005f * player.skillPassing) * d;
                }
            }
        }

        //speed
        if (timer < 0.2 * Const.SECOND) {
            //horizontal
            if (player.inputDevice.fire11) {
                ball.v += (960.0f + 2 * player.skillShooting) / Const.SECOND;
            }

            //vertical
            float f = 1.0f;
            if (player.inputDevice.value) {
                if (Math.abs(angle_diff) < 67.5f) {
                    f = (isPassing == IP_FALSE) ? 0.5f : 0;
                } else if (Math.abs(angle_diff) < 112.5f) {
                    f = 1.0f;
                } else {
                    f = 1.33f;
                }
            }

            ball.vz = f * (120.0f + ball.v * (1 - 0.05f * player.skillShooting) * timer / Const.SECOND);

            //spin
            if (player.inputDevice.value) {
                if ((Math.abs(angle_diff) > 22.5f) && (Math.abs(angle_diff) < 157.5f)) {
                    ball.s += 130.0f / Const.SECOND * Math.signum(angle_diff);
                }
            }
        }

        player.animationStandRun();

    }

    @Override
    State checkConditions() {
        if (timer > 0.35 * Const.SECOND) {
            return player.fsm.stateStandRun;
        }

        return null;
    }

    @Override
    void entryActions() {
        super.entryActions();
        isPassing = IP_UNKNOWN;
        ball = player.match.ball;

        ball.v = 190.0f;
        ball.a = player.kickAngle;

        player.match.listener.kickSound(1.0f * player.match.settings.sfxVolume);

    }

    @Override
    void exitActions() {
        if (player.team.usesAutomaticInputDevice()) {
            player.inputDevice = player.ai;
        }
    }

}
