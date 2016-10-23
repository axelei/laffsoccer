package com.ysoccer.android.ysdemo.match;


public class PlayerStateBenchSitting extends PlayerState {

    public PlayerStateBenchSitting(Player player) {
        super(player);
        id = PlayerFsm.STATE_BENCH_SITTING;
    }

    @Override
    void doActions() {
        super.doActions();

        if (player.targetDistance() > 1) {
            player.v = 200;
            player.a = player.targetAngle();
        } else {
            player.v = 0;
            player.a = 0;
        }

        player.animationStandRun();
    }

    @Override
    void entryActions() {
        super.entryActions();

        player.tx = Const.BENCH_X;
        if ((1 - 2 * player.team.index) == player.match.benchSide) {
            player.ty = Const.BENCH_Y_UP + 14 * (player.index - Const.TEAM_SIZE) + 46;
        } else {
            player.ty = Const.BENCH_Y_DOWN + 14 * (player.index - Const.TEAM_SIZE) + 46;
        }
        player.v = 0;
        player.a = 0;
        player.animationStandRun();
    }

}
