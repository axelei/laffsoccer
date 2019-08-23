package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_INTRO;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_STARTING_POSITIONS;

class MatchStateIntro extends MatchState {

    private final int enterDelay = GLGame.VIRTUAL_REFRESH_RATE / 16;

    MatchStateIntro(MatchFsm fsm) {
        super(STATE_INTRO, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.clock = 0;
        fsm.matchCompleted = false;
        match.setIntroPositions();
        match.resetData();

        Assets.Sounds.introId = Assets.Sounds.intro.play(Assets.Sounds.volume / 100f);
        Assets.Sounds.crowdId = Assets.Sounds.crowd.play(Assets.Sounds.volume / 100f);
        Assets.Sounds.crowd.setLooping(Assets.Sounds.crowdId, true);
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

            matchRenderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
            if (timer < GLGame.VIRTUAL_REFRESH_RATE) {
                matchRenderer.updateCameraY(ActionCamera.CF_NONE, 0, 0, false);
            } else {
                matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL, 0, false);
            }

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (match.enterPlayersFinished(timer, enterDelay)) {
            if ((match.team[HOME].fire1Down() != null)
                    || (match.team[AWAY].fire1Down() != null)
                    || (timer >= 5 * GLGame.VIRTUAL_REFRESH_RATE)) {
                fsm.pushAction(NEW_FOREGROUND, STATE_STARTING_POSITIONS);
                return;
            }
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

    @Override
    void render() {
        super.render();
        matchRenderer.drawRosters();
    }
}
