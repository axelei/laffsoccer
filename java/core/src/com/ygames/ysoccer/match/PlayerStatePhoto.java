package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PHOTO;

class PlayerStatePhoto extends PlayerState {

    PlayerStatePhoto(PlayerFsm fsm) {
        super(STATE_PHOTO, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        if (player.role == Player.Role.GOALKEEPER) {
            player.fmx = 2;
            player.fmy = 16;
        } else {
            if (player.index == 9) {
                player.fmx = 2;
            } else {
                player.fmx = player.index % 2;
            }
            player.fmy = 14;
        }

    }
}
