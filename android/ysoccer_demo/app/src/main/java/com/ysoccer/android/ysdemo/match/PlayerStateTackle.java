package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

public class PlayerStateTackle extends PlayerState {

    private Ball ball;
    private boolean hit;

    public PlayerStateTackle(Player player) {
        super(player);
        id = PlayerFsm.STATE_TACKLE;
    }

    @Override
    void doActions() {
        super.doActions();

        if (!hit) {
            if ((ball.z < 5) && (player.ballDistance < 18)) {
                float angle = Emath.aTan2(ball.y - player.y, ball.x - player.x);
                float angleDiff = Math
                        .abs((((angle - player.a + 540) % 360)) - 180);
                if ((angleDiff <= 90)
                        && (player.ballDistance * Emath.sin(angleDiff) <= (8 + 0.3f * player.skillTackling))) {

                    ball.setOwner(player);
                    ball.v = player.v * (1 + 0.02f * player.skillTackling);
                    hit = true;

                    if ((player.inputDevice.value)
                            && (Math.abs((((player.a - player.inputDevice.angle + 540) % 360)) - 180) < 67.5)) {
                        ball.a = player.inputDevice.angle;
                    } else {
                        ball.a = player.a;
                    }

                    // sound
                    player.match.listener.kickSound(0.1f * (1 + 0.03f * timer) * player.match.settings.sfxVolume);
                }
            }
        }

        player.v -= (20 + player.match.settings.grass.friction) / Const.SECOND * Math.sqrt(Math.abs(player.v));

        // animation
        player.fmx = Math.round((((player.a + 360) % 360)) / 45) % 8;
        player.fmy = 4;

    }

    @Override
    State checkConditions() {
        if (player.v < 30) {
            return player.fsm.stateStandRun;
        }
        return null;
    }

    @Override
    void entryActions() {
        super.entryActions();

        ball = player.match.ball;
        hit = false;

        player.v = 1.4f * (player.speed) * (1 + 0.02f * player.skillTackling);
    }

}
