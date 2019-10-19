package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FREE_KICK_ANGLE;

class PlayerStateFreeKickAngle extends PlayerState {

    PlayerStateFreeKickAngle(PlayerFsm fsm) {
        super(STATE_FREE_KICK_ANGLE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.setBallOwner(player);
    }

    @Override
    void doActions() {
        super.doActions();

        if (player.inputDevice.value) {
            player.a = player.inputDevice.angle;
        }
        player.x = ball.x - 7 * EMath.cos(player.a);
        player.y = ball.y - 7 * EMath.sin(player.a);
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
