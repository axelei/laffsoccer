package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Widget;

class DiyTournamentCalendar extends GLScreen {

    private Tournament tournament;

    DiyTournamentCalendar(GLGame game, Tournament tournament) {
        super(game);
        this.tournament = tournament;

        background = game.stateBackground;
        Widget w;

        w = new TitleBar("DIY TOURNAMENT CALENDAR", game.stateColor.body);
        widgets.add(w);
    }
}
