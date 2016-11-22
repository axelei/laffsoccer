package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Match;

class MatchScreen extends GLScreen {

    Match match;
    boolean matchStarted;
    boolean matchPaused;

    float timer;

    MatchScreen(GLGame game, Match match) {
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

        match.fsm.getMatchRenderer().render(game);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        match.fsm.getMatchRenderer().resize(width, height, game.settings);
    }
}
