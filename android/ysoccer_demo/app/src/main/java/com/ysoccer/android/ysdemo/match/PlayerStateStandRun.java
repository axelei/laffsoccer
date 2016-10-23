package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

public class PlayerStateStandRun extends PlayerState {

    private Ball ball;

    public PlayerStateStandRun(Player player) {
        super(player);
        id = PlayerFsm.STATE_STAND_RUN;
    }

    @Override
    void doActions() {
        super.doActions();

        player.getPossession();

        // ball control
        if ((ball.owner == player) && (ball.z < Const.PLAYER_H)) {

            // ball2player angle
            float angle = Emath.aTan2(ball.y - player.y, ball.x - player.x);

            // player - ball angle difference
            float angle_diff = Math
                    .abs(((angle - player.a + 540.0f) % 360.0f) - 180.0f);

            // if angle diff = 0 then push the ball
            if ((angle_diff <= 22.5) && (player.ballDistance < 11)) {
                if (player.inputDevice.value) {
                    ball.v = Math.max(ball.v,
                            (1 + 0.03f * ((player.ballDistance < 11) ? 1 : 0))
                                    * player.v);
                    ball.a = player.a;
                }
                // if changing direction keep control only if ball if near
            } else if (((angle_diff <= 67.5) && (player.ballDistance < 11))
                    || (player.ballDistance < 6)) {
                ball.x = player.x + (1.06f - 0.01f * player.skillControl)
                        * player.ballDistance * Emath.cos(player.a);
                ball.y = player.y + (1.06f - 0.01f * player.skillControl)
                        * player.ballDistance * Emath.sin(player.a);
            }

        }

        // movement
        if (player.inputDevice.value) {
            player.v = player.speed
                    * (1 - 0.1f * ((player == ball.owner) ? 1 : 0));
            player.a = player.inputDevice.angle;
        } else {
            player.v = 0;
        }

        player.animationStandRun();

    }

    @Override
    State checkConditions() {

        if ((player.role == Player.GOALKEEPER)
                && (player == player.team.lineup.get(0))
                && (player.team.near1 != player)
                && (player.inputDevice == player.ai)) {
            return player.fsm.stateKeeperPositioning;
        }

        // player fired
        if (player.inputDevice.fire1Down()) {

            // if in possession then kick
            if (ball.owner == player) {
                if (player.v > 0 && ball.z < 8) {
                    player.kickAngle = player.a;
                    return player.fsm.stateKick;
                }
                // else head or tackle
            } else if (player.frameDistance < Const.BALL_PREDICTION) {
                if (ball.prediction[player.frameDistance].z > 0.6f * Const.PLAYER_H) {
                    return player.fsm.stateHead;
                } else if (ball.prediction[player.frameDistance].z < 6) {
                    if ((player.v > 0) && (player.ballDistance < 100)) {
                        return player.fsm.stateTackle;
                    }
                }
            } else {
                // release input device
                if (player.team.usesAutomaticInputDevice()) {
                    player.inputDevice = player.ai;
                }
            }
        }
        return null;
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball = player.match.ball;
    }
}
