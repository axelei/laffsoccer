package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_RED_CARD;

class PlayerStateRedCard extends PlayerState {

    public PlayerStateRedCard(PlayerFsm fsm) {
        super(STATE_RED_CARD, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        player.v = 0;
        player.a = 90;
    }

    @Override
    void doActions() {
        super.doActions();

        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (timer > Const.SECOND) {
            return fsm.stateIdle;
        }
        return null;
    }
}
