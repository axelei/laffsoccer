package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KICK_OFF;

class PlayerStateKickOff extends PlayerState {

    PlayerStateKickOff(PlayerFsm fsm) {
        super(STATE_KICK_OFF, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        scene.setBallOwner(player);

        // prevent kicking backwards
        if (player.inputDevice.y1 != player.team.side) {
            if (player.inputDevice.value) {
                player.a = player.inputDevice.angle;
                player.x = ball.x - 7 * EMath.cos(player.a) + 1;
                player.y = ball.y - 7 * EMath.sin(player.a) + 1;
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

    @Override
    void entryActions() {
        super.entryActions();
        player.x = ball.x - 7 * player.team.side + 1;
        player.y = ball.y + 1;
        player.v = 0;
        player.a = 90 * (1 - player.team.side);
    }
}
