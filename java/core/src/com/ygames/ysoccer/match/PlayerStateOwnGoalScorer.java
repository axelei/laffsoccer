package com.ygames.ysoccer.match;

class PlayerStateOwnGoalScorer extends PlayerState {

    PlayerStateOwnGoalScorer(Player player) {
        super(PlayerFsm.STATE_OWN_GOAL_SCORER, player);
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        if (player.role == Player.Role.GOALKEEPER) {
            player.fmx = 2 + 2 * (1 - player.match.ball.ySide);
            player.fmy = 1;
        } else {
            player.fmy = 14 + (1 - player.match.ball.ySide) / 2;
            player.fmx = 3;
        }
    }
}
