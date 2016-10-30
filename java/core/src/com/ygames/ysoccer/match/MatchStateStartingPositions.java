package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGame;

public class MatchStateStartingPositions extends MatchState {

    boolean move;

    public MatchStateStartingPositions(MatchCore match) {
        super(match);
        id = MatchFsm.STATE_STARTING_POSITIONS;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = false;
        match.renderer.displayBallOwner = false;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = true;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = true;

        match.setStartingPositions();
        match.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GlGame.SUBFRAME_DURATION) {

            move = match.updatePlayers(false);

            match.nextSubframe();

            match.save();

            timeLeft -= GlGame.SUBFRAME_DURATION;
        }
    }
}
