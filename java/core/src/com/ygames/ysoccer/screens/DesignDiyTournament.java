package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Tournament;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.math.Emath;

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

        w = new WeatherButton();
        widgets.add(w);

        w = new AbortButton();
        widgets.add(w);
    }

    private class TournamentNameButton extends InputButton {

        TournamentNameButton() {
            setGeometry((game.gui.WIDTH - 940) / 2, 120, 940, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText(tournament.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(24);
        }

        @Override
        public void onChanged() {
            tournament.name = text;
        }
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setGeometry(game.gui.WIDTH / 2 - 470, 165, 236, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            tournament.weather = Competition.Weather.values()[Emath.rotate(tournament.weather, Competition.Weather.BY_SEASON, Competition.Weather.BY_PITCH_TYPE, 1)];
            setDirty(true);
            // TODO seasonStartButton.setDirty(true);
            // TODO seasonSeparatorButton.setDirty(true);
            // TODO seasonEndButton.setDirty(true);
            // TODO pitchTypeButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(tournament.getWeatherLabel()));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColors(0xC8000E);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
