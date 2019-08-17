package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

class PlayerStateKeeperKickAngle extends PlayerState {

    private Ball ball;

    PlayerStateKeeperKickAngle(Player player) {
        super(player);
        id = PlayerFsm.STATE_KEEPER_KICK_ANGLE;
    }

    @Override
    void doActions() {
        super.doActions();

        //prevent kicking backwards
        if (player.inputDevice.y1 != ball.ySide) {
            if (player.inputDevice.value) {
                player.a = player.inputDevice.angle;
            }
        }

        ball.setPosition(player.x + 5 * Emath.cos(player.a), player.y + 5 * Emath.sin(player.a), 12);

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
        ball.setHolder(player);
        player.v = 0;
        player.a = (90 * ball.ySide + 180) % 360;
    }

    @Override
    void exitActions() {
        ball.setHolder(null);
    }
}
