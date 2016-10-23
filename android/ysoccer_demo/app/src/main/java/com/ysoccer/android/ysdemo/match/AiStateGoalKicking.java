package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;

public class AiStateGoalKicking extends AiState {

	public AiStateGoalKicking(Ai ai) {
		super(ai);
		id = AiFsm.STATE_GOAL_KICKING;
	}

	@Override
	void doActions() {
		super.doActions();

		ai.x0 = 0;
		ai.y0 = 0;
		ai.fire10 = Emath.isIn(timer, 1.0f * GLGame.VIRTUAL_REFRATE,
				1.05f * GLGame.VIRTUAL_REFRATE);
	}

	@Override
	State checkConditions() {
		if (timer > 1.5f * GLGame.VIRTUAL_REFRATE) {
			return ai.fsm.stateIdle;
		}
		return null;
	}

}
