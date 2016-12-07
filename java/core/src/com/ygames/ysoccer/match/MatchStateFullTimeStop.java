package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

class MatchStateFullTimeStop extends MatchState {

    MatchStateFullTimeStop(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_FULL_TIME_STOP;
    }

    @Override
    void entryActions() {
        super.entryActions();

        matchRenderer.displayControlledPlayer = false;
        matchRenderer.displayBallOwner = false;
        matchRenderer.displayGoalScorer = false;
        matchRenderer.displayTime = true;
        matchRenderer.displayWindVane = true;
        matchRenderer.displayScore = false;
        matchRenderer.displayStatistics = false;
        matchRenderer.displayRadar = true;

        match.clock = match.length;

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

            match.updatePlayers(false);

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
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_END_POSITIONS);
            return;
        }
    }
}
