package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FINAL_CELEBRATION;

class PlayerStateFinalCelebration extends PlayerState {

    enum Step {RUNNING, DIVING, QUIT}

    private Step step;

    public PlayerStateFinalCelebration(PlayerFsm fsm) {
        super(STATE_FINAL_CELEBRATION, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        step = Step.RUNNING;
        player.v = 130 + EMath.rand(0, 30);

        float d = 200 + EMath.rand(0, 30);

        player.tx = player.x;
        player.ty = player.y + d * EMath.sin(player.a);
    }

    @Override
    void doActions() {
        super.doActions();

        switch (step) {
            case RUNNING:
                player.animationStandRun();

                if (player.targetDistance() < 1.5f) {
                    step = Step.DIVING;
                    player.fmy = 7;
                }
                break;

            case DIVING:
                player.v -= (20 + player.scene.settings.grass.friction) / Const.SECOND * Math.sqrt(Math.abs(player.v));
                if (player.v < 0) {
                    player.v = 0;
                    step = Step.QUIT;
                }
                break;
        }
    }

    @Override
    State checkConditions() {
        if (step == Step.QUIT) {
            return fsm.stateIdle;
        }
        return null;
    }
}