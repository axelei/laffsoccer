package com.ygames.ysoccer.match;

class PlayerStateDown extends PlayerState {

    PlayerStateDown(Player player) {
        super(player);
        id = PlayerFsm.STATE_DOWN;
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        player.fmy = 7;
    }

    @Override
    PlayerState checkConditions() {
        if (timer > 3f * Const.SECOND) {
            return player.fsm.stateStandRun;
        }
        return null;
    }
}
