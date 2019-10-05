package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_KICK_ANGLE;

class PlayerStateKeeperKickAngle extends PlayerState {

    PlayerStateKeeperKickAngle(PlayerFsm fsm) {
        super(STATE_KEEPER_KICK_ANGLE, fsm);
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

        ball.setPosition(player.x + 5 * EMath.cos(player.a), player.y + 5 * EMath.sin(player.a), 12);

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

    @Override
    void entryActions() {
        super.entryActions();
        ball.setHolder(player);
        player.v = 0;
        player.a = (90 * ball.ySide + 180) % 360;
    }

    @Override
    void exitActions() {
        ball.setHolder(null);
    }
}
