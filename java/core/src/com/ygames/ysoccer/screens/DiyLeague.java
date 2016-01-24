package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.math.Emath;

import java.util.Calendar;

public class DiyLeague extends GlScreen {

    public DiyLeague(GlGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_competition.jpg");

        game.competition = new League();
        game.competition.name = Assets.strings.get("DIY LEAGUE");

        Widget w;

        w = new TitleButton();
        widgets.add(w);

        w = new LeagueNameButton();
        widgets.add(w);

        selectedWidget = w;

        w = new SeasonPitchTypeButton();
        widgets.add(w);

        w = new SeasonStartButton();
        widgets.add(w);
    }

    class TitleButton extends Button {

        public TitleButton() {
            setGeometry((game.settings.GUI_WIDTH - 520) / 2, 30, 520, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(Assets.strings.get("DESIGN DIY LEAGUE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class LeagueNameButton extends InputButton {

        public LeagueNameButton() {
            setGeometry((game.settings.GUI_WIDTH - 700) / 2, 120, 700, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText(game.competition.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(24);
        }

        @Override
        public void onUpdate() {
            game.competition.name = text;
        }
    }

    class SeasonPitchTypeButton extends Button {

        public SeasonPitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 165, 350, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
            update();
        }

        @Override
        public void onFire1Down() {
            game.competition.bySeason = !game.competition.bySeason;
            update();
        }

        @Override
        public void update() {
            setText(Assets.strings.get(game.competition.bySeason ? "SEASON" : "PITCH TYPE"));
        }
    }

    class SeasonStartButton extends Button {
        public SeasonStartButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 10, 165, 180, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateSeasonStart(1);
        }

        @Override
        public void onFire1Hold() {
            updateSeasonStart(1);
        }

        @Override
        public void onFire2Down() {
            updateSeasonStart(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSeasonStart(-1);
        }

        private void updateSeasonStart(int n) {
            game.competition.seasonStart = Emath.rotate(game.competition.seasonStart, Calendar.JANUARY, Calendar.DECEMBER, n);
        }

        @Override
        public void update() {
            setText(Assets.monthNames.get(game.competition.seasonStart));
        }
    }
}
