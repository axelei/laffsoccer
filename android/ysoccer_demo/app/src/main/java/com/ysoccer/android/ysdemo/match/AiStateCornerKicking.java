package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;

public class AiStateCornerKicking extends AiState {

    public AiStateCornerKicking(Ai ai) {
        super(ai);
        id = AiFsm.STATE_CORNER_KICKING;
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = Emath.isIn(timer, 0.5f * GLGame.VIRTUAL_REFRATE,
                0.55f * GLGame.VIRTUAL_REFRATE) ? player.team.side : 0;
        ai.y0 = 0;
        ai.fire10 = Emath.isIn(timer, 1.0f * GLGame.VIRTUAL_REFRATE,
                1.05f * GLGame.VIRTUAL_REFRATE);
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.fsm.getState();
        if (playerState.checkId(PlayerFsm.STATE_CORNER_KICK_ANGLE)) {
            return null;
        }
        if (playerState.checkId(PlayerFsm.STATE_CORNER_KICK_SPEED)) {
            return null;
        }
        return ai.fsm.stateIdle;
    }

}
