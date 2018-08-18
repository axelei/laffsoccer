package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.tournament.Tournament;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

class InfoTournament extends GLScreen {

    private Tournament tournament;

    InfoTournament(GLGame game) {
        super(game);

        background = game.stateBackground;

        tournament = (Tournament) game.competition;
        Widget w;

        w = new TitleBar(tournament.name, game.stateColor.body);
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }
}
