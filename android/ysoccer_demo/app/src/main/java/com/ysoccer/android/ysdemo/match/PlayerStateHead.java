package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

public class PlayerStateHead extends PlayerState {

    private Ball ball;
    private boolean hit;

    public PlayerStateHead(Player player) {
        super(player);
        id = PlayerFsm.STATE_HEAD;
    }

    @Override
    void doActions() {
        super.doActions();

        if (!hit) {
            if (Emath.isIn(ball.z, player.z + Const.PLAYER_H - Const.BALL_R,
                    player.z + Const.PLAYER_H + Const.BALL_R)) {
                if (player.ballDistance < 2 * Const.BALL_R) {

                    ball.setOwner(player);
                    ball.v = Math.max(170 + 7 * player.skillHeading,
                            1.5f * player.v);
                    ball.vz = 100 - 8 * player.skillHeading + player.vz;
                    hit = true;

                    player.match.listener.kickSound(0.1f * (1 + 0.03f * timer) * player.match.settings.sfxVolume);

                    if (player.inputDevice.value) {
                        ball.a = player.inputDevice.angle;
                    } else {
                        ball.a = player.a;
                    }
                }
            }
        }

        // animation
        player.fmx = Math.round((((player.a + 360) % 360)) / 45) % 8;
        if (Math.abs(player.vz) < 0.1f) {
            player.fmy = 6;
        } else {
            player.fmy = 5;
        }

    }

    @Override
    State checkConditions() {
        if (player.vz == 0) {
            return player.fsm.stateStandRun;
        }
        return null;
    }

    @Override
    void entryActions() {
        super.entryActions();

        ball = player.match.ball;
        hit = false;

        if (player.v > 0) {
            player.v = 0.5f * (player.speed) * (1 + 0.1f * player.skillHeading);
        }

        player.vz = 90 + 5 * player.skillHeading;
    }

}
