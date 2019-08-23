package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_END;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HIGHLIGHTS;

class MatchStateHighlights extends MatchState {

    private int subframe0;
    private int position;
    private boolean keySlow;
    private boolean slowMotion;

    MatchStateHighlights(MatchFsm fsm) {
        super(STATE_HIGHLIGHTS, fsm);

        displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        // position of current frame inside the highlights vector
        position = 0;

        // slow motion
        keySlow = false;
        slowMotion = false;

        // store initial frame
        subframe0 = match.subframe;

        match.recorder.loadHighlight(matchRenderer);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // toggle slow-motion
        if (Gdx.input.isKeyPressed(Input.Keys.R) && !keySlow) {
            slowMotion = !slowMotion;
        }
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);

        int speed = GLGame.SUBFRAMES;

        // slow motion
        if (slowMotion) {
            speed = GLGame.SUBFRAMES / 2;
        }

        position += speed;
        if (position > Const.REPLAY_SUBFRAMES) {
            position = Const.REPLAY_SUBFRAMES;
        }

        match.subframe = (subframe0 + position) % Const.REPLAY_SUBFRAMES;
    }

    @Override
    void checkConditions() {

        // quit on ESC
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quit();
            return;
        }

        // quit on fire button
        if ((match.team[HOME].fire1Up() != null)
                || (match.team[AWAY].fire1Up() != null)) {
            quit();
            return;
        }

        // quit on finish
        if (position == Const.REPLAY_SUBFRAMES) {
            match.recorder.nextHighlight();
            if (match.recorder.hasEnded()) {
                quit();
                return;
            } else {
                fsm.pushAction(FADE_OUT);
                fsm.pushAction(NEW_FOREGROUND, STATE_HIGHLIGHTS);
                fsm.pushAction(FADE_IN);
                return;
            }
        }
    }

    void quit() {
        fsm.pushAction(FADE_OUT);
        fsm.pushAction(NEW_FOREGROUND, STATE_END);
        fsm.pushAction(FADE_IN);
    }
}
