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
import com.ygames.ysoccer.match.Time;
import com.ygames.ysoccer.math.Emath;

import java.util.Calendar;

public class DiyLeague extends GlScreen {

    League league;
    Widget seasonStartButton;
    Widget seasonSeparatorButton;
    Widget seasonEndButton;
    Widget pitchTypeButton;

    public DiyLeague(GlGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_competition.jpg");

        league = new League();
        league.name = Assets.strings.get("DIY LEAGUE");

        game.competition = league;

        Widget w;

        w = new TitleButton();
        widgets.add(w);

        w = new LeagueNameButton();
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

        selectedWidget = w;

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

        w = new BenchSizeLabel();
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
            setText(league.name, Font.Align.CENTER, Assets.font14);
            setEntryLimit(24);
        }

        @Override
        public void onUpdate() {
            league.name = text;
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
            league.bySeason = !league.bySeason;
            setChanged(true);
            seasonStartButton.setChanged(true);
            seasonSeparatorButton.setChanged(true);
            seasonEndButton.setChanged(true);
            pitchTypeButton.setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(league.bySeason ? "SEASON" : "PITCH TYPE"));
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
            league.seasonStart = Emath.rotate(league.seasonStart, Calendar.JANUARY, Calendar.DECEMBER, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Time.monthNames[league.seasonStart]));
            setVisible(league.bySeason);
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
            setVisible(league.bySeason);
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
            league.seasonEnd = Emath.rotate(league.seasonEnd, Calendar.JANUARY, Calendar.DECEMBER, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Time.monthNames[league.seasonEnd]));
            setVisible(league.bySeason);
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
            league.pitchType = Emath.rotate(league.pitchType, 0, Pitch.RANDOM, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Pitch.names[league.pitchType]));
            setVisible(!league.bySeason);
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

    class TimeButton extends Button {

        public TimeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 210, 240, 36);
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
            league.time = Emath.rotate(league.time, Time.DAY, Time.NIGHT, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(Assets.strings.get(Time.names[league.time]));
        }
    }

    class NumberOfTeamsLabel extends Button {

        public NumberOfTeamsLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 255, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("NUMBER OF TEAMS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class NumberOfTeamsButton extends Button {

        public NumberOfTeamsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 255, 240, 36);
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
            league.numberOfTeams = Emath.slide(league.numberOfTeams, 2, 24, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(league.numberOfTeams);
        }
    }

    class PlayEachTeamLabel extends Button {

        public PlayEachTeamLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 300, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("PLAY EACH TEAM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PlayEachTeamButton extends Button {

        public PlayEachTeamButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 300, 240, 36);
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
            league.rounds = Emath.slide(league.rounds, 1, 10, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(((char) 215) + "" + league.rounds);
        }
    }

    class PointsForAWinLabel extends Button {

        public PointsForAWinLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 345, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("POINTS FOR A WIN"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PointsForAWinButton extends Button {

        public PointsForAWinButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 345, 240, 36);
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
            league.pointsForAWin = Emath.rotate(league.pointsForAWin, 2, 3, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(league.pointsForAWin);
        }
    }

    class SubstitutesLabel extends Button {

        public SubstitutesLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 390, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesButton extends Button {

        public SubstitutesButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 390, 240, 36);
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
            league.substitutions = Emath.slide(league.substitutions, 2, league.benchSize, n);
            setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(league.substitutions);
        }
    }

    class BenchSizeLabel extends Button {

        public BenchSizeLabel() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 435, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
