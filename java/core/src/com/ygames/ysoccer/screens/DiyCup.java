package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Time;
import com.ygames.ysoccer.math.Emath;

import java.util.Calendar;

public class DiyCup extends GlScreen {

    Cup cup;
    Widget seasonStartButton;
    Widget seasonSeparatorButton;
    Widget seasonEndButton;
    Widget pitchTypeButton;

    public DiyCup(GlGame game) {
        super(game);

        background = game.stateBackground;

        cup = new Cup();
        cup.name = Assets.strings.get("DIY CUP");
        cup.category = Competition.Category.DIY_COMPETITION;

        Widget w;

        w = new TitleButton();
        widgets.add(w);

        w = new CupNameButton();
        widgets.add(w);

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

        w = new TimeButton();
        widgets.add(w);

        w = new SubstitutesLabel();
        widgets.add(w);

        selectedWidget = w;
    }

    class TitleButton extends Button {

        public TitleButton() {
            setGeometry((game.settings.GUI_WIDTH - 520) / 2, 30, 520, 40);
            setColors(game.stateColor);
            setText(Assets.strings.get("DESIGN DIY CUP"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class CupNameButton extends InputButton {

        public CupNameButton() {
            setGeometry((game.settings.GUI_WIDTH - 940) / 2, 101, 940, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText(cup.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(24);
        }

        @Override
        public void onUpdate() {
            cup.name = text;
            cup.longName = text;
        }
    }

    class SeasonPitchTypeButton extends Button {

        public SeasonPitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 144, 230, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            cup.bySeason = !cup.bySeason;
            setChanged(true);
            seasonStartButton.setChanged(true);
            seasonSeparatorButton.setChanged(true);
            seasonEndButton.setChanged(true);
            pitchTypeButton.setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(cup.getBySeasonLabel()));
        }
    }

    class SeasonStartButton extends Button {

        public SeasonStartButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 240, 144, 180, 36);
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
            cup.seasonStart = Emath.rotate(cup.seasonStart, Calendar.JANUARY, Calendar.DECEMBER, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Time.monthNames[cup.seasonStart]));
            setVisible(cup.bySeason);
        }
    }

    class SeasonSeparatorButton extends Button {

        public SeasonSeparatorButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 60, 144, 40, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setVisible(cup.bySeason);
        }
    }

    class SeasonEndButton extends Button {

        public SeasonEndButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 20, 144, 180, 36);
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
            cup.seasonEnd = Emath.rotate(cup.seasonEnd, Calendar.JANUARY, Calendar.DECEMBER, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Time.monthNames[cup.seasonEnd]));
            setVisible(cup.bySeason);
        }
    }

    class PitchTypeButton extends Button {

        public PitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 240, 144, 400, 36);
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
            cup.pitchType = Emath.rotate(cup.pitchType, 0, Pitch.RANDOM, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Pitch.names[cup.pitchType]));
            setVisible(!cup.bySeason);
        }
    }

    class TimeLabel extends Button {

        public TimeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 170, 144, 140, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class TimeButton extends Button {

        public TimeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 310, 144, 160, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateTime(1);
        }

        @Override
        public void onFire2Down() {
            updateTime(-1);
        }

        private void updateTime(int n) {
            cup.time = Emath.rotate(cup.time, Time.DAY, Time.NIGHT, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Time.names[cup.time]));
        }
    }

    class SubstitutesLabel extends Button {

        public SubstitutesLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 187, 320, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
