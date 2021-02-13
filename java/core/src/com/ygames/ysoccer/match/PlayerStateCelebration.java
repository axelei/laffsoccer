package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CELEBRATION;

class PlayerStateCelebration extends PlayerState {

    int duration;

    PlayerStateCelebration(PlayerFsm fsm) {
        super(STATE_CELEBRATION, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        duration = EMath.rand(400, 500);
    }

    @Override
    void doActions() {
        super.doActions();

        player.fmy = ((timer % duration) > (duration / 3)) ? 8 : 1;
    }
}
