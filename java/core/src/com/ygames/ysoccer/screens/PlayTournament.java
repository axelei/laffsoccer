package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;

public class PlayTournament extends GLScreen {

    private Tournament tournament;

    PlayTournament(GLGame game) {
        super(game);

        tournament = (Tournament) game.competition;

        background = game.stateBackground;
    }
}
