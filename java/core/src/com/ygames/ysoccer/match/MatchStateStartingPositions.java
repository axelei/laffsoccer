package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

class MatchStateStartingPositions extends MatchState {

    private boolean move;

    MatchStateStartingPositions(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_STARTING_POSITIONS;

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.setStartingPositions();
        match.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            move = match.updatePlayers(false);

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_FAST);
            matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_KICK_OFF);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_PAUSE);
            return;
        }
    }
}
