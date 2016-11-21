package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.MatchCore;

class MatchScreen extends GLScreen {

    MatchCore match;
    boolean matchStarted;
    boolean matchPaused;

    float timer;

    MatchScreen(GLGame game, MatchCore match) {
        super(game);
        playMenuMusic = false;
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

        match.renderer.render(game);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        match.renderer.resize(width, height, game.settings);
    }
}
