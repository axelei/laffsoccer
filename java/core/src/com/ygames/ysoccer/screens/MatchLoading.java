package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchLoading extends GLScreen {

    Match match;

    MatchLoading(GLGame game, Team homeTeam, Team awayTeam, MatchSettings matchSettings, Competition competition) {
        super(game);
        playMenuMusic = false;

        matchSettings.setup();

        Team[] team = {homeTeam, awayTeam};

        match = competition.getMatch();
        match.init(game, team, matchSettings, competition);

        game.unsetMouse();

        Assets.loadStadium(matchSettings);
        Assets.loadCrowd(match.team[Match.HOME]);
        Assets.loadBall(matchSettings);
        Assets.loadCornerFlags(matchSettings);
        for (int t = HOME; t <= AWAY; t++) {
            int len = team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = team[t].lineup.get(i);
                if (player.role == Player.Role.GOALKEEPER) {
                    Assets.loadKeeper(player);
                } else {
                    Assets.loadPlayer(player);
                }
                Assets.loadHair(player);
            }
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.setScreen(new MatchScreen(game, match));
    }
}
