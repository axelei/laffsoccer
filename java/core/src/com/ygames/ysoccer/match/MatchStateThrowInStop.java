package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

class MatchStateThrowInStop extends MatchState {

    private boolean move;

    MatchStateThrowInStop(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_THROW_IN_STOP;

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(match.settings.soundVolume / 100f);

        match.throwInX = match.ball.xSide * Const.TOUCH_LINE;
        match.throwInY = match.ball.y;

        match.resetAutomaticInputDevices();
        match.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);
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

            move = match.updatePlayers(true);
            match.updateTeamTactics();

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
            matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            match.ball.setPosition(match.throwInX, match.throwInY, 0);
            match.ball.updatePrediction();

            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_THROW_IN);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            match.fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_PAUSE);
            return;
        }
    }
}
