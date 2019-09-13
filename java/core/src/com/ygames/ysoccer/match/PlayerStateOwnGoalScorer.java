package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OWN_GOAL_SCORER;

class PlayerStateOwnGoalScorer extends PlayerState {

    PlayerStateOwnGoalScorer(PlayerFsm fsm) {
        super(STATE_OWN_GOAL_SCORER, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        if (player.role == Player.Role.GOALKEEPER) {
            player.fmx = 2 + 2 * (1 - player.ball.ySide);
            player.fmy = 1;
        } else {
            player.fmy = 14 + (1 - player.ball.ySide) / 2;
            player.fmx = 3;
        }
    }
}
