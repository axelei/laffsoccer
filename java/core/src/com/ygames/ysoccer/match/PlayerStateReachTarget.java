package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;

class PlayerStateReachTarget extends PlayerState {

    PlayerStateReachTarget(PlayerFsm fsm) {
        super(STATE_REACH_TARGET, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        player.v = 180 + 3 * player.skills.speed;
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
        player.watchPosition(player.team.match.pointOfInterest);
    }
}
