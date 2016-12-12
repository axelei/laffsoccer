package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

class MatchStateCornerKick extends MatchState {

    private Team cornerKickTeam;
    private Player cornerKickPlayer;
    private boolean isKicking;

    MatchStateCornerKick(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_CORNER_KICK;

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayScore = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        cornerKickTeam = match.team[1 - match.ball.ownerLast.team.index];

        matchRenderer.actionCamera.offx = -30 * match.ball.xSide;
        matchRenderer.actionCamera.offy = -30 * match.ball.ySide;

        isKicking = false;

        cornerKickTeam.updateFrameDistance();
        cornerKickTeam.findNearest();
        cornerKickPlayer = cornerKickTeam.near1;

        cornerKickPlayer.setTarget(match.ball.x + 7 * match.ball.xSide, match.ball.y);
        cornerKickPlayer.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        boolean move = true;
        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();

            move = match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_FAST);
            matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            Assets.Sounds.whistle.play(match.settings.soundVolume / 100f);

            cornerKickPlayer.fsm.setState(PlayerFsm.STATE_CORNER_KICK_ANGLE);
            if (cornerKickPlayer.team.usesAutomaticInputDevice()) {
                cornerKickPlayer.inputDevice = cornerKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    void checkConditions() {
        if (match.ball.v > 0) {
            match.setPlayersState(PlayerFsm.STATE_STAND_RUN, cornerKickPlayer);
            fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_MAIN);
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
