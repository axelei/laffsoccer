package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_TACKLE;

class PlayerStateTackle extends PlayerState {

    private boolean hit;

    PlayerStateTackle(PlayerFsm fsm) {
        super(STATE_TACKLE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        hit = false;

        player.v = 1.4f * (player.speed) * (1 + 0.02f * player.skills.tackling);
        player.x += 10 * EMath.cos(player.a);
        player.y += 10 * EMath.sin(player.a);
        player.fmx = Math.round((((player.a + 360) % 360)) / 45) % 8;
        player.fmy = 4;
    }

    @Override
    void doActions() {
        super.doActions();

        // feet collision
        if (!hit) {
            if (ball.z < 5 && player.ballDistance < 7) {
                float angle = EMath.aTan2(ball.y - player.y, ball.x - player.x);
                float angleDiff = EMath.angleDiff(angle, player.a);
                if ((angleDiff <= 90)
                        && (player.ballDistance * EMath.sin(angleDiff) <= (8 + 0.3f * player.skills.tackling))) {

                    scene.setBallOwner(player);
                    ball.v = player.v * (1 + 0.02f * player.skills.tackling);
                    hit = true;

                    if ((player.inputDevice.value)
                            && (Math.abs((((player.a - player.inputDevice.angle + 540) % 360)) - 180) < 67.5)) {
                        ball.a = player.inputDevice.angle;
                    } else {
                        ball.a = player.a;
                    }

                    Assets.Sounds.kick.play(0.1f * (1 + 0.03f * timer) * Assets.Sounds.volume / 100f);
                }
            }
        }

        // body collision
        if (!hit) {
            float bodyX = player.x - 9 * EMath.cos(player.a);
            float bodyY = player.y - 9 * EMath.sin(player.a);
            if (ball.z < 7 && EMath.dist(ball.x, ball.y, bodyX, bodyY) < 9) {
                float angle = EMath.aTan2(ball.y - bodyY, ball.x - bodyX);
                float angleDiff = EMath.angleDiff(angle, player.a);
                ball.v = player.v * Math.abs(EMath.cos(angleDiff));
                ball.a = angle;
                hit = true;
            }
        }

        player.v -= (20 + player.scene.settings.grass.friction) / Const.SECOND * Math.sqrt(Math.abs(player.v));

        // animation
        player.fmx = Math.round((((player.a + 360) % 360)) / 45) % 8;
        player.fmy = 4;
    }

    @Override
    State checkConditions() {
        if (player.v < 30) {
            return fsm.stateStandRun;
        }
        return null;
    }
}
