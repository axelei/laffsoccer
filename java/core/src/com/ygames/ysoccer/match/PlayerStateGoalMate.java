package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_GOAL_MATE;

class PlayerStateGoalMate extends PlayerState {

    private Goal goal;

    PlayerStateGoalMate(PlayerFsm fsm) {
        super(STATE_GOAL_MATE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        goal = player.getMatch().goals.get(player.getMatch().goals.size() - 1);
    }

    @Override
    void doActions() {
        super.doActions();

        player.tx = goal.player.x + (10 + (player.number % 20)) * EMath.cos(30 * player.number);
        player.ty = goal.player.y + (10 + (player.number % 20)) * EMath.sin(30 * player.number);

        if (player.targetDistance() < 1.5f) {
            player.v = 0;
        } else {
            player.v = player.speed;
            player.a = player.targetAngle();
        }
        player.animationStandRun();
    }
}
