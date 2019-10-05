package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.Round;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Label;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.framework.EMath;

class DesignDiyCup extends GLScreen {

    Cup cup;
    private Widget seasonStartButton;
    private Widget seasonSeparatorButton;
    private Widget seasonEndButton;
    private Widget pitchTypeButton;
    private Widget substitutesButton;
    private Widget awayGoalsButton;
    private Widget[] roundNameLabels = new Widget[6];
    private Widget[] roundTeamsLabels = new Widget[6];
    private Widget[] roundLegsButtons = new Widget[6];
    private Widget[] roundExtraTimeButtons = new Widget[6];
    private Widget[] roundPenaltiesButtons = new Widget[6];

    DesignDiyCup(GLGame game) {
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

        w = new TitleBar(Assets.strings.get("DESIGN DIY CUP"), game.stateColor.body);
        widgets.add(w);

        w = new CupNameButton();
        widgets.add(w);

        w = new WeatherButton();
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
        setSelectedWidget(w);

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
            roundNameLabels[i] = w;

            w = new RoundTeamsLabel(i);
            widgets.add(w);
            roundTeamsLabels[i] = w;

            w = new RoundLegsButton(i);
            widgets.add(w);
            roundLegsButtons[i] = w;

            w = new RoundExtraTimeButton(i);
            widgets.add(w);
            roundExtraTimeButtons[i] = w;

            w = new RoundPenaltiesButton(i);
            widgets.add(w);
            roundPenaltiesButtons[i] = w;
        }

        w = new OkButton();
        widgets.add(w);

        w = new AbortButton();
        widgets.add(w);
    }

    private class CupNameButton extends InputButton {

        CupNameButton() {
            setGeometry((game.gui.WIDTH - 940) / 2, 120, 940, 36);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText(cup.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(24);
        }

        @Override
        public void onChanged() {
            cup.name = text;
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
            cup.weather = Competition.Weather.values()[EMath.rotate(cup.weather, Competition.Weather.BY_SEASON, Competition.Weather.BY_PITCH_TYPE, 1)];
            setDirty(true);
            seasonStartButton.setDirty(true);
            seasonSeparatorButton.setDirty(true);
            seasonEndButton.setDirty(true);
            pitchTypeButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(cup.getWeatherLabel()));
        }
    }

    private class SeasonStartButton extends Button {

        SeasonStartButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 165, 176, 36);
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
            cup.seasonStart = Month.values()[EMath.rotate(cup.seasonStart, Month.JANUARY, Month.DECEMBER, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Month.getLabel(cup.seasonStart)));
            setVisible(cup.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class SeasonSeparatorButton extends Button {

        SeasonSeparatorButton() {
            setGeometry(game.gui.WIDTH / 2 - 54, 165, 36, 36);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(cup.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class SeasonEndButton extends Button {

        SeasonEndButton() {
            setGeometry(game.gui.WIDTH / 2 - 16, 165, 176, 36);
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
            cup.seasonEnd = Month.values()[EMath.rotate(cup.seasonEnd, Month.JANUARY, Month.DECEMBER, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Month.getLabel(cup.seasonEnd)));
            setVisible(cup.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 165, 392, 36);
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
            cup.pitchType = Pitch.Type.values()[EMath.rotate(cup.pitchType, Pitch.Type.FROZEN, Pitch.Type.RANDOM, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Pitch.names[cup.pitchType.ordinal()]));
            setVisible(cup.weather == Competition.Weather.BY_PITCH_TYPE);
        }
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 170, 165, 140, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 165, 158, 36);
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
            cup.time = MatchSettings.Time.values()[EMath.rotate(cup.time, MatchSettings.Time.DAY, MatchSettings.Time.NIGHT, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(MatchSettings.getTimeLabel(cup.time)));
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 210, 305, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 - 163, 210, 158, 36);
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
            cup.substitutions = EMath.slide(cup.substitutions, 2, cup.benchSize, n);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(cup.substitutions);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 + 5, 210, 305, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 + 312, 210, 158, 36);
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
            cup.benchSize = EMath.slide(cup.benchSize, 2, 12, n);
            cup.substitutions = Math.min(cup.substitutions, cup.benchSize);
            setDirty(true);
            substitutesButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(cup.benchSize);
        }
    }

    private class RoundsLabel extends Button {

        RoundsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 255, 236, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("ROUNDS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class RoundsButton extends Button {

        RoundsButton() {
            setGeometry(game.gui.WIDTH / 2 - 232, 255, 158, 36);
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
            setDirty(true);
            for (int i = 0; i < 6; i++) {
                roundNameLabels[i].setDirty(true);
                roundTeamsLabels[i].setDirty(true);
                roundLegsButtons[i].setDirty(true);
                roundExtraTimeButtons[i].setDirty(true);
                roundPenaltiesButtons[i].setDirty(true);
            }
            awayGoalsButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(cup.rounds.size());
        }
    }

    private class AwayGoalsLabel extends Button {

        AwayGoalsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 64, 255, 228, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class AwayGoalsButton extends Button {

        AwayGoalsButton() {
            setGeometry(game.gui.WIDTH / 2 + 166, 255, 304, 36);
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
            cup.awayGoals = Competition.AwayGoals.values()[EMath.rotate(cup.awayGoals.ordinal(), 0, 2, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(cup.getAwayGoalsLabel(cup.awayGoals)));
            setVisible(cup.hasTwoLegsRound());
        }
    }

    private class TeamsLabel extends Label {

        TeamsLabel() {
            setText(Assets.strings.get("TEAMS"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 - 186, 325);
        }
    }

    private class DescriptionLabel extends Label {

        DescriptionLabel() {
            setText(Assets.strings.get("DESCRIPTION"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 + 115, 325);
        }
    }

    private class RoundNameLabel extends Button {

        private int round;

        RoundNameLabel(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 470, 344 + 34 * round, 248, 32);
            setColors(0x800000, 0xB40000, 0x400000);
            setText("", Font.Align.LEFT, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).name));
            }
        }
    }

    private class RoundTeamsLabel extends Button {

        private int round;

        RoundTeamsLabel(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 212, 344 + 34 * round, 50, 32);
            setColors(0x800000, 0xB40000, 0x400000);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setText(cup.getRoundTeams(round));
            setVisible(round < cup.rounds.size());
        }
    }

    private class RoundLegsButton extends Button {

        private int round;

        RoundLegsButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 152, 344 + 34 * round, 138, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateLegs(1);
        }

        @Override
        public void onFire2Down() {
            updateLegs(-1);
        }

        private void updateLegs(int n) {
            cup.rounds.get(round).numberOfLegs = EMath.rotate(cup.rounds.get(round).numberOfLegs, 1, 2, n);
            setDirty(true);
            awayGoalsButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getLegsLabel()));
            }
        }
    }

    private class RoundExtraTimeButton extends Button {

        private int round;

        RoundExtraTimeButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 - 12, 344 + 34 * round, 240, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateExtraTime(1);
        }

        @Override
        public void onFire2Down() {
            updateExtraTime(-1);
        }

        private void updateExtraTime(int n) {
            cup.rounds.get(round).extraTime = Round.ExtraTime.values()[EMath.rotate(cup.rounds.get(round).extraTime.ordinal(), 0, 2, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getExtraTimeLabel()));
            }
        }
    }

    private class RoundPenaltiesButton extends Button {

        private int round;

        RoundPenaltiesButton(int round) {
            this.round = round;
            setGeometry(game.gui.WIDTH / 2 + 230, 344 + 34 * round, 240, 32);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updatePenalties(1);
        }

        @Override
        public void onFire2Down() {
            updatePenalties(-1);
        }

        private void updatePenalties(int n) {
            cup.rounds.get(round).penalties = Round.Penalties.values()[EMath.rotate(cup.rounds.get(round).penalties.ordinal(), 0, 2, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setVisible(round < cup.rounds.size());
            if (visible) {
                setText(Assets.strings.get(cup.rounds.get(round).getPenaltiesLabel()));
            }
        }
    }

    private class OkButton extends Button {

        OkButton() {
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setGeometry((game.gui.WIDTH - 180) / 2, 605, 180, 38);
            setText(Assets.strings.get("OK"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            navigation.folder = Assets.teamsRootFolder;
            navigation.competition = cup;
            game.setScreen(new SelectFolder(game));
        }
    }

    private class AbortButton extends Button {

        AbortButton() {
            setColor(0xC8000E);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
