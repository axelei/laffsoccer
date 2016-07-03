package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
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
    Widget substitutesButton;
    Widget awayGoalsButton;
    Widget[] roundNameButtons = new Widget[6];

    public DiyCup(GlGame game) {
        super(game);

        background = game.stateBackground;

        cup = new Cup();
        cup.name = Assets.strings.get("DIY CUP");
        cup.category = Competition.Category.DIY_COMPETITION;
        cup.addRound();
        cup.addRound();
        cup.addRound();
        cup.addRound();

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

        w = new SubstitutesButton();
        widgets.add(w);
        substitutesButton = w;

        w = new BenchSizeLabel();
        widgets.add(w);

        w = new BenchSizeButton();
        widgets.add(w);

        w = new RoundsLabel();
        widgets.add(w);

        w = new RoundsButton();
        widgets.add(w);
        selectedWidget = w;

        w = new AwayGoalsLabel();
        widgets.add(w);

        w = new AwayGoalsButton();
        widgets.add(w);
        awayGoalsButton = w;

        w = new TeamsLabel();
        widgets.add(w);

        w = new DescriptionLabel();
        widgets.add(w);

        // rounds
        for (int i = 0; i < 6; i++) {
            w = new RoundNameLabel(i);
            widgets.add(w);
            roundNameButtons[i] = w;
        }
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 144, 236, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 232, 144, 176, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 54, 144, 36, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 16, 144, 176, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 232, 144, 392, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 + 312, 144, 158, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 187, 305, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesButton extends Button {

        public SubstitutesButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 163, 187, 158, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateSubstitutes(1);
        }

        @Override
        public void onFire1Hold() {
            updateSubstitutes(1);
        }

        @Override
        public void onFire2Down() {
            updateSubstitutes(-1);
        }

        @Override
        public void onFire2Hold() {
            updateSubstitutes(-1);
        }

        private void updateSubstitutes(int n) {
            cup.substitutions = Emath.slide(cup.substitutions, 2, cup.benchSize, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(cup.substitutions);
        }
    }

    class BenchSizeLabel extends Button {

        public BenchSizeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 5, 187, 305, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeButton extends Button {

        public BenchSizeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 312, 187, 158, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateBenchSize(1);
        }

        @Override
        public void onFire1Hold() {
            updateBenchSize(1);
        }

        @Override
        public void onFire2Down() {
            updateBenchSize(-1);
        }

        @Override
        public void onFire2Hold() {
            updateBenchSize(-1);
        }

        private void updateBenchSize(int n) {
            cup.benchSize = Emath.slide(cup.benchSize, 2, 5, n);
            cup.substitutions = Math.min(cup.substitutions, cup.benchSize);
            setChanged(true);
            substitutesButton.setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(cup.benchSize);
        }
    }

    public class RoundsLabel extends Button {

        public RoundsLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 230, 236, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("ROUNDS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    public class RoundsButton extends Button {

        public RoundsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 232, 230, 158, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateRounds(1);
        }

        @Override
        public void onFire1Hold() {
            updateRounds(1);
        }

        @Override
        public void onFire2Down() {
            updateRounds(-1);
        }

        @Override
        public void onFire2Hold() {
            updateRounds(-1);
        }

        private void updateRounds(int n) {
            if (n == 1) {
                cup.addRound();
            } else {
                cup.removeRound();
            }
            setChanged(true);
            for (int i = 0; i < 6; i++) {
                roundNameButtons[i].setChanged(true);
            }
        }

        @Override
        public void onUpdate() {
            setText(cup.rounds.size());
        }
    }

    public class AwayGoalsLabel extends Button {

        public AwayGoalsLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 64, 230, 228, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    public class AwayGoalsButton extends Button {

        public AwayGoalsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 166, 230, 304, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateAwayGoals(1);
        }

        @Override
        public void onFire2Down() {
            updateAwayGoals(-1);
        }

        private void updateAwayGoals(int n) {
            cup.awayGoals = Cup.AwayGoals.values()[Emath.rotate(cup.awayGoals.ordinal(), 0, 2, n)];
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(cup.getAwayGoalsLabel()));
            setVisible(cup.hasTwoLegsRound());
        }
    }

    class TeamsLabel extends Label {

        public TeamsLabel() {
            setText(Assets.strings.get("TEAMS"), Font.Align.CENTER, Assets.font14);
            setPosition(game.settings.GUI_WIDTH / 2 - 175, 296);
        }
    }

    class DescriptionLabel extends Label {

        public DescriptionLabel() {
            setText(Assets.strings.get("DESCRIPTION"), Font.Align.CENTER, Assets.font14);
            setPosition(game.settings.GUI_WIDTH / 2 - 10 + 125, 296);
        }
    }

    class RoundNameLabel extends Button {

        private int round;

        public RoundNameLabel(int round) {
            this.round = round;
            setGeometry(game.settings.GUI_WIDTH / 2 - 470, 315 + 34 * round, 250, 32);
            setColors(0x800000, 0xB40000, 0x400000);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void onUpdate() {
            setText(cup.getRoundName(round));
            setVisible(round < cup.rounds.size());
        }
    }
}
