package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

class PlayerStateCornerKickAngle extends PlayerState {

    PlayerStateCornerKickAngle(Player player) {
        super(PlayerFsm.STATE_CORNER_KICK_ANGLE, player);
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball.setOwner(player);
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
            float angle = Emath.aTan2(y, x);

            if (value) {
                player.a = angle;
            }
        }
        player.x = ball.x - 7 * Emath.cos(player.a);
        player.y = ball.y - 7 * Emath.sin(player.a);
        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.inputDevice.fire1Down()) {
            return player.fsm.stateCornerKickSpeed;
        }
        return null;
    }
}
