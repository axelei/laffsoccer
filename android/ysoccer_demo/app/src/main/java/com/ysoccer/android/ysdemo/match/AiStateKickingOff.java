package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;

public class AiStateKickingOff extends AiState {

	public AiStateKickingOff(Ai ai) {
		super(ai);
		id = AiFsm.STATE_KICKING_OFF;
	}
	
	@Override
	void doActions() {
		super.doActions();
		ai.x0 = player.team.side;
		ai.fire10 = Emath.isIn(timer, 1.0f*GLGame.VIRTUAL_REFRATE, 1.05f*GLGame.VIRTUAL_REFRATE);
	}
	
	@Override
	State checkConditions() {
		if (timer > 1.2f*GLGame.VIRTUAL_REFRATE) {
			return ai.fsm.stateIdle;
		}
		return null;
	}
	
}
