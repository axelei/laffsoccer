package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

public class PlayerStateCornerKickAngle extends PlayerState {

    private Ball ball;

    public PlayerStateCornerKickAngle(Player player) {
        super(player);
        id = PlayerFsm.STATE_CORNER_KICK_ANGLE;
    }

    @Override
    void entryActions() {
        super.entryActions();
        ball = player.match.ball;
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
