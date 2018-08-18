package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;

class InfoTournament extends GLScreen {

    private Tournament tournament;

    InfoTournament(GLGame game) {
        super(game);

        background = game.stateBackground;

        tournament = (Tournament) game.competition;
    }
}
