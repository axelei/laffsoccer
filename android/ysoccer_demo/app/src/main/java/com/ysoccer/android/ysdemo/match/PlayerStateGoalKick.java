package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

public class PlayerStateGoalKick extends PlayerState {

    private Ball ball;

    public PlayerStateGoalKick(Player player) {
        super(player);
        id = PlayerFsm.STATE_GOAL_KICK;
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball = player.match.ball;
    }

    @Override
    void doActions() {
        super.doActions();

        ball.setOwner(player);

        //prevent kicking backwards
        if (player.inputDevice.y1 != ball.ySide) {
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

}
