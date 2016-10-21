package com.ygames.ysoccer.match;

class PlayerStatePhoto extends PlayerState {

    PlayerStatePhoto(Player player) {
        super(player);
        id = PlayerFsm.STATE_PHOTO;
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
