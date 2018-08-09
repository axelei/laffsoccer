package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Tournament;
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
import com.ygames.ysoccer.math.Emath;

class DesignDiyTournament extends GLScreen {

    Tournament tournament;
    private Widget seasonStartButton;
    private Widget seasonSeparatorButton;
    private Widget seasonEndButton;
    private Widget pitchTypeButton;
    private Widget substitutesButton;
    private Widget awayGoalsButton;

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

        w = new AwayGoalsLabel();
        widgets.add(w);

        w = new AwayGoalsButton();
        widgets.add(w);
        awayGoalsButton = w;

        w = new TeamsLabel();
        widgets.add(w);

        w = new OkButton();
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
            seasonStartButton.setDirty(true);
            seasonSeparatorButton.setDirty(true);
            seasonEndButton.setDirty(true);
            pitchTypeButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(tournament.getWeatherLabel()));
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
            tournament.seasonStart = Month.values()[Emath.rotate(tournament.seasonStart, Month.JANUARY, Month.DECEMBER, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Month.getLabel(tournament.seasonStart)));
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
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
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
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
            tournament.seasonEnd = Month.values()[Emath.rotate(tournament.seasonEnd, Month.JANUARY, Month.DECEMBER, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Month.getLabel(tournament.seasonEnd)));
            setVisible(tournament.weather == Competition.Weather.BY_SEASON);
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
            tournament.pitchType = Pitch.Type.values()[Emath.rotate(tournament.pitchType.ordinal(), 0, Pitch.Type.RANDOM.ordinal(), n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Pitch.names[tournament.pitchType.ordinal()]));
            setVisible(tournament.weather == Competition.Weather.BY_PITCH_TYPE);
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
            tournament.time = MatchSettings.Time.values()[Emath.rotate(tournament.time, MatchSettings.Time.DAY, MatchSettings.Time.NIGHT, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(MatchSettings.getTimeLabel(tournament.time)));
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 470, 210, 244, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 - 224, 210, 52, 36);
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
            tournament.substitutions = Emath.slide(tournament.substitutions, 2, tournament.benchSize, n);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(tournament.substitutions);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 170, 210, 94, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES.FROM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 - 74, 210, 52, 36);
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
            tournament.benchSize = Emath.slide(tournament.benchSize, 2, 12, n);
            tournament.substitutions = Math.min(tournament.substitutions, tournament.benchSize);
            setDirty(true);
            substitutesButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(tournament.benchSize);
        }
    }

    private class AwayGoalsLabel extends Button {

        AwayGoalsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 12, 210, 206, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("AWAY GOALS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class AwayGoalsButton extends Button {

        AwayGoalsButton() {
            setGeometry(game.gui.WIDTH / 2 + 196, 210, 274, 36);
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
            tournament.awayGoals = Competition.AwayGoals.values()[Emath.rotate(tournament.awayGoals.ordinal(), 0, 2, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(tournament.getAwayGoalsLabel(tournament.awayGoals)));
            setVisible(tournament.hasTwoLegsRound());
        }
    }

    private class TeamsLabel extends Label {

        TeamsLabel() {
            setText(Assets.strings.get("TEAMS"), Font.Align.CENTER, Assets.font14);
            setPosition(game.gui.WIDTH / 2 - 406, 280);
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
            navigation.competition = tournament;
            game.setScreen(new SelectFolder(game));
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
