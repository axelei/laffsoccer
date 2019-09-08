package com.ygames.ysoccer.screens;

import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.annotation.HiddenCommand;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchScreen extends GLScreen {

    Match match;
    private boolean matchStarted;
    private boolean matchPaused;
    private boolean matchEnded;

    private Console console;

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

        if (game.settings.development) {
            console = new GUIConsole();
            console.setSizePercent(25, 100);
            console.setPositionPercent(0, 0);
            console.setHoverAlpha(0.9f);
            console.setNoHoverAlpha(0.9f);
            console.setCommandExecutor(new ConsoleCommandExecutor());
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
            match.fsm.getMatchRenderer().render();
        }

        if (!matchEnded && game.settings.development) {
            console.draw();
            matchPaused = console.isVisible();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        match.fsm.getMatchRenderer().resize(width, height, match.settings.zoom);

        if (game.settings.development) {
            console.refresh();
        }
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

        if (matchCompleted) {
            match.competition.matchCompleted();
        } else if (match.clock > 0) {
            match.competition.matchInterrupted();
        }

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
                game.setScreen(new DevTools(game));
                break;
        }
    }

    class ConsoleCommandExecutor extends CommandExecutor {

        @HiddenCommand
        public void gravity() {
            console.log("gravity " + Const.GRAVITY);
        }

        public void gravity(float f) {
            Const.GRAVITY = f;
        }

        @HiddenCommand
        public void airFriction() {
            console.log("airFriction " + Const.AIR_FRICTION);
        }

        public void airFriction(float f) {
            Const.AIR_FRICTION = f;
        }

        @HiddenCommand
        public void spinFactor() {
            console.log("spinFactor " + Const.SPIN_FACTOR);
        }

        public void spinFactor(float f) {
            Const.SPIN_FACTOR = f;
        }

        @HiddenCommand
        public void spinDampening() {
            console.log("spinDampening " + Const.SPIN_DAMPENING);
        }

        public void spinDampening(float f) {
            Const.SPIN_DAMPENING = f;
        }

        @HiddenCommand
        public void bounce() {
            console.log("bounce " + Const.BOUNCE);
        }

        public void bounce(float f) {
            Const.BOUNCE = f;
        }

        @HiddenCommand
        public void runAnimation() {
            console.log("runAnimation " + Const.PLAYER_RUN_ANIMATION);
        }

        public void runAnimation(float f) {
            Const.PLAYER_RUN_ANIMATION = f;
        }

        public void homePenalty() {
            Match match = MatchScreen.this.match;
            Team homeTeam = match.team[HOME];
            Player homePlayer = homeTeam.lineup.get(0);
            Team awayTeam = match.team[AWAY];
            Player awayPlayer = awayTeam.lineup.get(0);
            match.newFoul(awayPlayer, homePlayer);
        }

        public void awayPenalty() {
            Match match = MatchScreen.this.match;
            Team homeTeam = match.team[HOME];
            Player homePlayer = homeTeam.lineup.get(0);
            Team awayTeam = match.team[AWAY];
            Player awayPlayer = awayTeam.lineup.get(0);
            match.newFoul(homePlayer, awayPlayer);
        }
    }
}
