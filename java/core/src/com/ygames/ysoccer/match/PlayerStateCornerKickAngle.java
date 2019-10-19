package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CORNER_KICK_ANGLE;

class PlayerStateCornerKickAngle extends PlayerState {

    PlayerStateCornerKickAngle(PlayerFsm fsm) {
        super(STATE_CORNER_KICK_ANGLE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        scene.setBallOwner(player);
        player.a = 90 * (1 + ball.xSide);
    }

    @Override
    void doActions() {
        super.doActions();

        int x = player.inputDevice.x1;
        int y = player.inputDevice.y1;

        // prevent kicking out
        if ((x != ball.xSide) && (y != ball.ySide)) {
            boolean value = (x != 0) || (y != 0);
            float angle = EMath.aTan2(y, x);

            if (value) {
                player.a = angle;
            }
        }
        player.x = ball.x - 7 * EMath.cos(player.a);
        player.y = ball.y - 7 * EMath.sin(player.a);
        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.inputDevice.fire1Down()) {
            return fsm.stateCornerKickSpeed;
        }
        return null;
    }
}
