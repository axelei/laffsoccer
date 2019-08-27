package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HALF_TIME_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_STARTING_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;

class MatchStateHalfTimeEnter extends MatchState {

    private int enteringCounter;

    MatchStateHalfTimeEnter(MatchFsm fsm) {
        super(STATE_HALF_TIME_ENTER, fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.setStartingPositions();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
                if ((enteringCounter % 4) == 0 && enteringCounter / 4 < Const.TEAM_SIZE) {
                    for (int t = HOME; t <= AWAY; t++) {
                        int i = enteringCounter / 4;
                        Player player = match.team[t].lineup.get(i);
                        player.setState(STATE_REACH_TARGET);
                    }
                }
                enteringCounter += 1;
            }

            match.updatePlayers(false);

            match.nextSubframe();

            match.save();

            matchRenderer.updateCamera(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (enteringCounter / 4 == Const.TEAM_SIZE) {
            fsm.pushAction(NEW_FOREGROUND, STATE_STARTING_POSITIONS);
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
            fsm.pushAction(HOLD_FOREGROUND, STATE_PAUSE);
            return;
        }
    }
}
