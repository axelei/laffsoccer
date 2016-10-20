package com.ygames.ysoccer.match;

class MatchStateIntro extends MatchState {

    MatchStateIntro(MatchCore match) {
        super(match);
        id = MatchFsm.STATE_INTRO;
    }
}
