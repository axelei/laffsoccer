package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.badlogic.gdx.Input.Keys.F1;
import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HALF_EXTRA_TIME_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_STARTING_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHalfExtraTimeStop extends MatchState {

    MatchStateHalfExtraTimeStop(MatchFsm fsm) {
        super(STATE_HALF_EXTRA_TIME_STOP, fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.end.play(Assets.Sounds.volume / 100f);

        match.resetAutomaticInputDevices();
        match.setPlayersState(STATE_IDLE, null);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();

            match.updatePlayers(true);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE) {
            match.ball.setPosition(0, 0, 0);
            match.ball.updatePrediction();

            sceneRenderer.actionCamera.setOffset(0, 0);

            match.swapTeamSides();

            match.kickOffTeam = 1 - match.coinToss;

            match.period = Match.Period.SECOND_EXTRA_TIME;
            match.clock = match.length * 105 / 90;

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

        if (Gdx.input.isKeyPressed(F1)) {
            fsm.pushAction(HOLD_FOREGROUND, STATE_HELP);
            return;
        }
    }
}
