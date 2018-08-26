package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.competitions.tournament.groups.Groups;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Widget;

class DiyTournamentCalendar extends GLScreen {

    private Tournament tournament;

    private enum Mode {GROUPS_DISTRIBUTION, GROUP_MATCHES, KNOCKOUT_CALENDAR}

    private Mode mode;

    DiyTournamentCalendar(GLGame game, Tournament tournament) {
        super(game);
        this.tournament = tournament;

        background = game.stateBackground;
        Widget w;

        w = new TitleBar("DIY TOURNAMENT CALENDAR", game.stateColor.body);
        widgets.add(w);

        switch (tournament.getRound().type) {
            case GROUPS:
                Groups groups = (Groups) tournament.getRound();
                mode = Mode.GROUPS_DISTRIBUTION;
                break;
        }
    }
}
