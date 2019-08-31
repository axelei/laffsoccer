package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FREE_KICK_ANGLE;

class PlayerStateFreeKickAngle extends PlayerState {

    PlayerStateFreeKickAngle(PlayerFsm fsm) {
        super(STATE_FREE_KICK_ANGLE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball.setOwner(player);
    }

    @Override
    void doActions() {
        super.doActions();

        if (player.inputDevice.value) {
            player.a = player.inputDevice.angle;
        }
        player.x = ball.x - 7 * Emath.cos(player.a);
        player.y = ball.y - 7 * Emath.sin(player.a);
        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.inputDevice.fire1Down()) {
            return fsm.stateFreeKickSpeed;
        }
        return null;
    }
}
