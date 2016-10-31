package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

class MatchStateIntro extends MatchState {

    private final int enterDelay = GLGame.VIRTUAL_REFRESH_RATE / 16;

    MatchStateIntro(MatchCore match) {
        super(match);
        id = MatchFsm.STATE_INTRO;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.setIntroPositions();
        match.resetData();

        // TODO
        // match.listener.introSound(match.settings.sfxVolume);
        // match.listener.crowdSound(match.settings.sfxVolume);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        match.enterPlayers(timer - 1, enterDelay);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.updatePlayers(false);
            match.playersPhoto();

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (match.enterPlayersFinished(timer, enterDelay)) {
            if ((match.team[Match.HOME].fire1Down() != null)
                    || (match.team[Match.AWAY].fire1Down() != null)
                    || (timer >= 5 * GLGame.VIRTUAL_REFRESH_RATE)) {
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_STARTING_POSITIONS);
            }
        }
    }
}
