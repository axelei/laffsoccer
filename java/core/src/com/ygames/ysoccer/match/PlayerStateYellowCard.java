package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_YELLOW_CARD;

class PlayerStateYellowCard extends PlayerState {

    public PlayerStateYellowCard(PlayerFsm fsm) {
        super(STATE_YELLOW_CARD, fsm);
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
            if (player.getMatch().referee.isSentOff(player)) {
                return fsm.stateRedCard;
            } else {
                return fsm.stateIdle;
            }
        }
        return null;
    }
}
