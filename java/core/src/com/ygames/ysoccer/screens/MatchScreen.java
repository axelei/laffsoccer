package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchConsoleCommandExecutor;
import com.ygames.ysoccer.match.Player;

import java.util.Locale;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchScreen extends GLScreen {

    private final Match match;
    private boolean matchStarted;
    private boolean matchPaused;
    private boolean matchEnded;

    private Console console;

    MatchScreen(GLGame game, Match match) {
        super(game);
        this.match = match;

        playMenuMusic = false;
        usesMouse = false;

        matchStarted = false;
        matchPaused = false;
        matchEnded = false;
        game.glGraphics.light = 0;

        match.listener = new Match.MatchListener() {
            public void quitMatch(boolean matchCompleted) {
                quit(matchCompleted);
            }
        };

        if (Settings.development) {
            console = new GUIConsole();
            console.setSizePercent(25, 100);
            console.setPositionPercent(0, 0);
            console.setHoverAlpha(0.9f);
            console.setNoHoverAlpha(0.9f);
            console.setCommandExecutor(new MatchConsoleCommandExecutor(match));
        }
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
            match.render();
        }

        if (!matchEnded && Settings.development) {
            console.draw();
            matchPaused = console.isVisible();
        }

        if (Settings.development && Settings.showJavaHeap) {
            batch.begin();
            Assets.font10.draw(batch, String.format(Locale.getDefault(), "%,d", Gdx.app.getJavaHeap()), game.gui.WIDTH - 120, 10, Font.Align.LEFT);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        match.resize(width, height);

        if (Settings.development) {
            console.refresh();
        }
    }

    private void quit(boolean matchCompleted) {
        matchEnded = true;

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

        if (matchCompleted) {
            match.competition.matchCompleted();
        } else if (match.clock > 0) {
            match.competition.matchInterrupted();
        }

        game.enableMouse();
        switch (match.competition.type) {
            case FRIENDLY:
                game.setScreen(new ReplayMatch(game, match));
                break;

            case LEAGUE:
                game.setScreen(new PlayLeague(game));
                break;

            case CUP:
                game.setScreen(new PlayCup(game));
                break;

            case TOURNAMENT:
                game.setScreen(new PlayTournament(game));
                break;

            case TEST_MATCH:
                game.setScreen(new DeveloperTools(game));
                break;
        }
    }
}
