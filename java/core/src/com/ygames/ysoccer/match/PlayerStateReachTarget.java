package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;

class PlayerStateReachTarget extends PlayerState {

    PlayerStateReachTarget(PlayerFsm fsm) {
        super(STATE_REACH_TARGET, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        player.v = 180 + EMath.rand(0, 30);
    }

    @Override
    void doActions() {
        super.doActions();

        player.a = player.targetAngle();
        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.targetDistance() < 1) {
            return fsm.stateIdle;
        }
        return null;
    }

    @Override
    void exitActions() {
        player.v = 0;
        player.watchPosition(player.scene.pointOfInterest);
    }
}
