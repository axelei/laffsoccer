package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.match.MatchCore;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Team;

class MatchLoading extends GlScreen {

    MatchCore matchCore;

    MatchLoading(GlGame game, Team homeTeam, Team awayTeam, MatchSettings matchSettings) {
        super(game);

        matchSettings.setup();

        Team[] team = {homeTeam, awayTeam};

        matchCore = new MatchCore(game, team, matchSettings);

        Gdx.graphics.setCursor(null);
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // TODO: game.setScreen(new MatchScreen(game, match));
    }
}
