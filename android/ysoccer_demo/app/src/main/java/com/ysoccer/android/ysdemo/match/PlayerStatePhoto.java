package com.ysoccer.android.ysdemo.match;

public class PlayerStatePhoto extends PlayerState {

    public PlayerStatePhoto(Player player) {
        super(player);
        id = PlayerFsm.STATE_PHOTO;
    }

    @Override
    void entryActions() {
        super.entryActions();

        if (player.role == Player.GOALKEEPER) {
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
