package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Player;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchScreen extends GLScreen {

    Match match;
    private boolean matchStarted;
    private boolean matchPaused;
    private boolean matchEnded;

    MatchScreen(GLGame game, Match match) {
        super(game);
        playMenuMusic = false;
        this.match = match;

        matchStarted = false;
        matchPaused = false;
        matchEnded = false;
        game.glGraphics.light = 0;

        match.listener = new Match.MatchListener() {
            public void quitMatch(boolean matchCompleted) {
                quit(matchCompleted);
            }
        };
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        if (!matchStarted) {
            match.start();
            matchStarted = true;
        }

        if (!matchPaused) {
            match.update(deltaTime);
        }

        if (!matchEnded) {
            match.fsm.getMatchRenderer().render(game);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        match.fsm.getMatchRenderer().resize(width, height, game.settings);
    }

    private void quit(boolean matchCompleted) {
        matchEnded = true;
        game.setMouse();

        for (int t = HOME; t <= AWAY; t++) {
            int len = match.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = match.team[t].lineup.get(i);
                if (player.role == Player.Role.GOALKEEPER) {
                    Assets.unloadKeeper(player);
                } else {
                    Assets.unloadPlayer(player);
                }
                Assets.unloadHair(player);
            }
            match.team[t].lineup.clear();
        }

        switch (match.competition.type) {
            case FRIENDLY:
                game.setScreen(new ReplayMatch(game, match));
                break;

            case LEAGUE:
                ((League) match.competition).addMatchToTable(match);
                game.setScreen(new PlayLeague(game));
                break;

            case CUP:
                game.setScreen(new PlayCup(game));
                break;
        }
    }
}
