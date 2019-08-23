package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_ANGLE;

class PlayerStatePenaltyKickAngle extends PlayerState {

    PlayerStatePenaltyKickAngle(PlayerFsm fsm, Player player) {
        super(STATE_PENALTY_KICK_ANGLE, fsm, player);
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball.setOwner(player);
        player.a = 90 * (2 - ball.ySide);
    }

    @Override
    void doActions() {
        super.doActions();

        // can only kick in the direction of the goal
        if (player.inputDevice.y1 == ball.ySide) {
            if (player.inputDevice.value) {
                player.a = player.inputDevice.angle;
            }
        }
        player.x = ball.x - 7 * Emath.cos(player.a);
        player.y = ball.y - 7 * Emath.sin(player.a);
        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.inputDevice.fire1Down()) {
            // TODO fsm.statePenaltyKickSpeed;
            return fsm.stateCornerKickSpeed;
        }
        return null;
    }
}
