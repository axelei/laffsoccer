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
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.math.Emath;

import java.util.Calendar;

public class DiyLeague extends GlScreen {

    Widget seasonStartButton;
    Widget seasonSeparatorButton;
    Widget seasonEndButton;
    Widget pitchTypeButton;

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
        seasonStartButton = w;

        w = new SeasonSeparatorButton();
        widgets.add(w);
        seasonSeparatorButton = w;

        w = new SeasonEndButton();
        widgets.add(w);
        seasonEndButton = w;

        w = new PitchTypeButton();
        widgets.add(w);
        pitchTypeButton = w;

        w = new TimeLabel();
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 165, 290, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.competition.bySeason = !game.competition.bySeason;
            setChanged(true);
            seasonStartButton.setChanged(true);
            seasonSeparatorButton.setChanged(true);
            seasonEndButton.setChanged(true);
            pitchTypeButton.setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(game.competition.bySeason ? "SEASON" : "PITCH TYPE"));
        }
    }

    class SeasonStartButton extends Button {

        public SeasonStartButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 50, 165, 180, 36);
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
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.monthNames.get(game.competition.seasonStart));
            setVisible(game.competition.bySeason);
        }
    }

    class SeasonSeparatorButton extends Button {

        public SeasonSeparatorButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 130, 165, 40, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText("-", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setVisible(game.competition.bySeason);
        }
    }

    class SeasonEndButton extends Button {

        public SeasonEndButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 170, 165, 180, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateSeasonEnd(1);
        }

        @Override
        public void onFire1Hold() {
            updateSeasonEnd(1);
        }

        @Override
        public void onFire2Down() {
            updateSeasonEnd(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSeasonEnd(-1);
        }

        private void updateSeasonEnd(int n) {
            game.competition.seasonEnd = Emath.rotate(game.competition.seasonEnd, Calendar.JANUARY, Calendar.DECEMBER, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.monthNames.get(game.competition.seasonEnd));
            setVisible(game.competition.bySeason);
        }
    }

    class PitchTypeButton extends Button {

        public PitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 50, 165, 400, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updatePitchType(1);
        }

        @Override
        public void onFire1Hold() {
            updatePitchType(1);
        }

        @Override
        public void onFire2Down() {
            updatePitchType(-1);
        }

        @Override
        public void onFire2Hold() {
            updatePitchType(-1);
        }

        private void updatePitchType(int n) {
            game.competition.pitchType = Emath.rotate(game.competition.pitchType, 0, Pitch.RANDOM, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.pitchNames.get(game.competition.pitchType));
            setVisible(!game.competition.bySeason);
        }
    }

    class TimeLabel extends Button {

        public TimeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 210, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
