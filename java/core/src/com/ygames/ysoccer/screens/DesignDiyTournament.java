package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Tournament;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;

class DesignDiyTournament extends GLScreen {

    Tournament tournament;

    DesignDiyTournament(GLGame game) {
        super(game);

        background = game.stateBackground;

        tournament = new Tournament();
        tournament.name = Assets.strings.get("DIY TOURNAMENT");
        tournament.category = Competition.Category.DIY_COMPETITION;

        Widget w;

        w = new TitleBar(Assets.strings.get("DESIGN DIY TOURNAMENT"), game.stateColor.body);
        widgets.add(w);

        w = new TournamentNameButton();
        widgets.add(w);
    }

    private class TournamentNameButton extends InputButton {

        TournamentNameButton() {
            setGeometry((game.gui.WIDTH - 700) / 2, 120, 700, 38);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText(tournament.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(24);
        }

        @Override
        public void onChanged() {
            tournament.name = text;
        }
    }
}
