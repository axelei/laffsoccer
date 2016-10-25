package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.match.MatchCore;

class MatchScreen extends GlScreen {

    MatchCore match;
    boolean matchStarted;
    boolean matchPaused;

    float timer;

    MatchScreen(GlGame game, MatchCore match) {
        super(game);
        this.match = match;

        matchStarted = false;
        matchPaused = false;
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        // TODO: wait texture loading instead
        timer += deltaTime;
        if (!matchStarted && timer > 2.0f) {
            match.start();
            matchStarted = true;
        }

        if (!matchPaused) {
            match.update(deltaTime);
        }
    }
}
