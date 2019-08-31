package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_GOAL_KICK;

class PlayerStateGoalKick extends PlayerState {

    PlayerStateGoalKick(PlayerFsm fsm) {
        super(STATE_GOAL_KICK, fsm);
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
            return fsm.stateKick;
        }
        return null;
    }
}
