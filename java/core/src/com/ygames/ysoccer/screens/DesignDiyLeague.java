package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.InputButton;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.framework.EMath;

class DesignDiyLeague extends GLScreen {

    League league;
    private Widget seasonStartButton;
    private Widget seasonSeparatorButton;
    private Widget seasonEndButton;
    private Widget pitchTypeButton;
    private Widget substitutesButton;

    DesignDiyLeague(GLGame game) {
        super(game);

        background = game.stateBackground;

        league = new League();
        league.name = Assets.strings.get("DIY LEAGUE");
        league.category = Competition.Category.DIY_COMPETITION;

        Widget w;

        w = new TitleBar(Assets.strings.get("DESIGN DIY LEAGUE"), game.stateColor.body);
        widgets.add(w);

        w = new LeagueNameButton();
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

        setSelectedWidget(w);

        w = new NumberOfTeamsLabel();
        widgets.add(w);

        w = new NumberOfTeamsButton();
        widgets.add(w);

        w = new PlayEachTeamLabel();
        widgets.add(w);

        w = new PlayEachTeamButton();
        widgets.add(w);

        w = new PointsForAWinLabel();
        widgets.add(w);

        w = new PointsForAWinButton();
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

        w = new OkButton();
        widgets.add(w);

        w = new AbortButton();
        widgets.add(w);
    }

    private class LeagueNameButton extends InputButton {

        LeagueNameButton() {
            setGeometry((game.gui.WIDTH - 700) / 2, 120, 700, 38);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText(league.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(24);
        }

        @Override
        public void onChanged() {
            league.name = text;
        }
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setGeometry(game.gui.WIDTH / 2 - 350, 175, 290, 38);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            league.weather = Competition.Weather.values()[EMath.rotate(league.weather, Competition.Weather.BY_SEASON, Competition.Weather.BY_PITCH_TYPE, 1)];
            setDirty(true);
            seasonStartButton.setDirty(true);
            seasonSeparatorButton.setDirty(true);
            seasonEndButton.setDirty(true);
            pitchTypeButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(league.getWeatherLabel()));
        }
    }

    private class SeasonStartButton extends Button {

        SeasonStartButton() {
            setGeometry(game.gui.WIDTH / 2 - 50, 175, 180, 38);
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
            league.seasonStart = Month.values()[EMath.rotate(league.seasonStart, Month.JANUARY, Month.DECEMBER, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Month.getLabel(league.seasonStart)));
            setVisible(league.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class SeasonSeparatorButton extends Button {

        SeasonSeparatorButton() {
            setGeometry(game.gui.WIDTH / 2 + 132, 175, 36, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            setVisible(league.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class SeasonEndButton extends Button {

        SeasonEndButton() {
            setGeometry(game.gui.WIDTH / 2 + 170, 175, 180, 38);
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
            league.seasonEnd = Month.values()[EMath.rotate(league.seasonEnd, Month.JANUARY, Month.DECEMBER, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Month.getLabel(league.seasonEnd)));
            setVisible(league.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 50, 175, 400, 38);
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
            league.pitchType = Pitch.Type.values()[EMath.rotate(league.pitchType, Pitch.Type.FROZEN, Pitch.Type.RANDOM, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(Pitch.names[league.pitchType.ordinal()]));
            setVisible(league.weather == Competition.Weather.BY_PITCH_TYPE);
        }
    }

    private class TimeLabel extends Button {

        TimeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 230, 440, 38);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 230, 240, 38);
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
            league.time = MatchSettings.Time.values()[EMath.rotate(league.time, MatchSettings.Time.DAY, MatchSettings.Time.NIGHT, n)];
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(Assets.strings.get(MatchSettings.getTimeLabel(league.time)));
        }
    }

    private class NumberOfTeamsLabel extends Button {

        NumberOfTeamsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 285, 440, 38);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("NUMBER OF TEAMS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class NumberOfTeamsButton extends Button {

        NumberOfTeamsButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 285, 240, 38);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updateNumberOfTeams(1);
        }

        @Override
        public void onFire1Hold() {
            updateNumberOfTeams(1);
        }

        @Override
        public void onFire2Down() {
            updateNumberOfTeams(-1);
        }

        @Override
        public void onFire2Hold() {
            updateNumberOfTeams(-1);
        }

        private void updateNumberOfTeams(int n) {
            league.numberOfTeams = EMath.slide(league.numberOfTeams, 2, 24, n);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(league.numberOfTeams);
        }
    }

    private class PlayEachTeamLabel extends Button {

        PlayEachTeamLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 340, 440, 38);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("PLAY EACH TEAM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PlayEachTeamButton extends Button {

        PlayEachTeamButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 340, 240, 38);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updatePlayEachTeam(1);
        }

        @Override
        public void onFire1Hold() {
            updatePlayEachTeam(1);
        }

        @Override
        public void onFire2Down() {
            updatePlayEachTeam(-1);
        }

        @Override
        public void onFire2Hold() {
            updatePlayEachTeam(-1);
        }

        private void updatePlayEachTeam(int n) {
            league.rounds = EMath.slide(league.rounds, 1, 10, n);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(((char) 215) + "" + league.rounds);
        }
    }

    private class PointsForAWinLabel extends Button {

        PointsForAWinLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 395, 440, 38);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("POINTS FOR A WIN"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PointsForAWinButton extends Button {

        PointsForAWinButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 395, 240, 38);
            setColors(0x1F1F95, 0x3030D4, 0x151563);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            updatePointsForAWin(1);
        }

        @Override
        public void onFire2Down() {
            updatePointsForAWin(-1);
        }

        private void updatePointsForAWin(int n) {
            league.pointsForAWin = EMath.rotate(league.pointsForAWin, 2, 3, n);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(league.pointsForAWin);
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 450, 440, 38);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 450, 240, 38);
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
            league.substitutions = EMath.slide(league.substitutions, 2, league.benchSize, n);
            setDirty(true);
        }

        @Override
        public void refresh() {
            setText(league.substitutions);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 505, 440, 38);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 505, 240, 38);
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
            league.benchSize = EMath.slide(league.benchSize, 2, 12, n);
            league.substitutions = Math.min(league.substitutions, league.benchSize);
            setDirty(true);
            substitutesButton.setDirty(true);
        }

        @Override
        public void refresh() {
            setText(league.benchSize);
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
            navigation.competition = league;
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
