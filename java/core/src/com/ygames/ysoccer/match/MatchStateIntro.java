package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGame;

class MatchStateIntro extends MatchState {

    private final int enterDelay = GlGame.VIRTUAL_REFRESH_RATE / 16;

    MatchStateIntro(MatchCore match) {
        super(match);
        id = MatchFsm.STATE_INTRO;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.setIntroPositions();
        match.resetData();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        match.enterPlayers(timer - 1, enterDelay);

        float timeLeft = deltaTime;
        while (timeLeft >= GlGame.SUBFRAME_DURATION) {

            match.updatePlayers(false);
            match.playersPhoto();

            match.nextSubframe();

            match.save();

            timeLeft -= GlGame.SUBFRAME_DURATION;
        }
    }
}
