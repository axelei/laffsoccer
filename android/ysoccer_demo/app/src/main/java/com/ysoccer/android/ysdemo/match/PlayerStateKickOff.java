package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

public class PlayerStateKickOff extends PlayerState {

    private Ball ball;

    public PlayerStateKickOff(Player player) {
        super(player);
        id = PlayerFsm.STATE_KICK_OFF;
    }

    @Override
    void doActions() {
        super.doActions();

        ball.setOwner(player);

        //prevent kicking backwards
        if (player.inputDevice.y1 != player.team.side) {
            if (player.inputDevice.value) {
                player.a = player.inputDevice.angle;
                player.x = ball.x - 7 * Emath.cos(player.a) + 1;
                player.y = ball.y - 7 * Emath.sin(player.a) + 1;
            }
        }

        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.inputDevice.fire1Down()) {
            player.kickAngle = player.a;
            return player.fsm.stateKick;
        }
        return null;
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball = player.match.ball;
        player.x = ball.x - 7 * player.team.side + 1;
        player.y = ball.y + 1;
        player.v = 0;
        player.a = 90 * (1 - player.team.side);
    }

}
