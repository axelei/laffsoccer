package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStateExtraTimeStop extends MatchState {

    MatchStateExtraTimeStop(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_EXTRA_TIME_STOP;

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.end.play(match.settings.soundVolume / 100f);

        match.resetAutomaticInputDevices();
        match.setPlayersState(PlayerFsm.STATE_IDLE, null);
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

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);
            matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE) {
            match.ball.setPosition(0, 0, 0);
            match.ball.updatePrediction();

            matchRenderer.actionCamera.offx = 0;
            matchRenderer.actionCamera.offy = 0;

            // redo coin toss
            match.coinToss = Assets.random.nextInt(2); // 0 = home begins, 1 = away begins
            match.kickOffTeam = match.coinToss;

            // reassign teams sides
            match.team[HOME].side = 1 - 2 * Assets.random.nextInt(2); // -1 = up, 1 = down
            match.team[AWAY].side = -match.team[HOME].side;

            match.period = Match.Period.FIRST_EXTRA_TIME;
            match.clock = match.length;

            fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_STARTING_POSITIONS);
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
