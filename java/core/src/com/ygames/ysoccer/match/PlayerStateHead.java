package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_HEAD;

class PlayerStateHead extends PlayerState {

    private boolean hit;
    private boolean jumped;
    private float v;

    PlayerStateHead(PlayerFsm fsm) {
        super(STATE_HEAD, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        if (!hit) {
            if (EMath.isIn(ball.z, player.z + Const.PLAYER_H - Const.BALL_R,
                    player.z + Const.PLAYER_H + Const.BALL_R)) {
                if (player.ballDistance < 2 * Const.BALL_R) {

                    scene.setBallOwner(player);
                    ball.v = Math.max(170 + 7 * player.skills.heading, 1.5f * player.v);
                    ball.vz = 100 - 8 * player.skills.heading + player.vz;
                    hit = true;

                    Assets.Sounds.kick.play(0.1f * (1 + 0.03f * timer) * Assets.Sounds.volume / 100f);

                    if (player.inputDevice.value) {
                        ball.a = player.inputDevice.angle;
                    } else {
                        ball.a = player.a;
                    }
                }
            }
        }

        if (!jumped && timer > 0.05f * Const.SECOND) {
            if (v > 0) {
                player.v = 0.5f * (player.speed) * (1 + 0.1f * player.skills.heading);
            }
            player.vz = 90 + 5 * player.skills.heading;
            jumped = true;
        }

        // animation
        player.fmx = Math.round((((player.a + 360) % 360)) / 45) % 8;

        if (jumped) {
            if (player.z > 0) {
                if (player.vz > 40) {
                    player.fmy = 5;
                } else if (player.vz > -30) {
                    player.fmy = 6;
                } else {
                    player.fmy = 5;
                }
            } else {
                player.fmy = 1;
            }
        }
    }

    @Override
    State checkConditions() {
        if (jumped && player.vz == 0) {
            return fsm.stateStandRun;
        }
        return null;
    }

    @Override
    void entryActions() {
        super.entryActions();

        hit = false;
        jumped = false;
        v = player.v;
        player.v = 0;

        player.fmy = 3;
    }
}
