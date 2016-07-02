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
import com.ygames.ysoccer.match.Time;
import com.ygames.ysoccer.math.Emath;

import java.util.Calendar;

public class DiyCup extends GlScreen {

    Cup cup;
    Widget seasonStartButton;
    Widget seasonSeparatorButton;

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
}
