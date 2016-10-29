package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGame;

class MatchStateIntro extends MatchState {

    MatchStateIntro(MatchCore match) {
        super(match);
        id = MatchFsm.STATE_INTRO;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GlGame.SUBFRAME_DURATION) {

            match.nextSubframe();

            timeLeft -= GlGame.SUBFRAME_DURATION;
        }
    }
}
