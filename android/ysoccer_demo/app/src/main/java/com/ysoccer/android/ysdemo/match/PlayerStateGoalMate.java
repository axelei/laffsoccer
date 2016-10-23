package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

class PlayerStateGoalMate extends PlayerState {

    private Goal goal;

    PlayerStateGoalMate(Player player) {
        super(player);
        id = PlayerFsm.STATE_GOAL_MATE;
    }

    @Override
    void entryActions() {
        super.entryActions();
        goal = player.match.goals.get(player.match.goals.size() - 1);
    }

    @Override
    void doActions() {
        super.doActions();

        player.tx = goal.player.x + (10 + (player.number % 20)) * Emath.cos(30 * player.number);
        player.ty = goal.player.y + (10 + (player.number % 20)) * Emath.sin(30 * player.number);

        if (player.targetDistance() < 1.5f) {
            player.v = 0;
        } else {
            player.v = player.speed;
            player.a = player.targetAngle();
        }
        player.animationStandRun();
    }
}
