package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Activity;
import com.ygames.ysoccer.competitions.League;
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

public class DiyLeague extends GlScreen {

    League league;
    Widget seasonStartButton;
    Widget seasonSeparatorButton;
    Widget seasonEndButton;
    Widget pitchTypeButton;
    Widget substitutesButton;

    public DiyLeague(GlGame game) {
        super(game);

        background = game.stateBackground;

        league = new League();
        league.name = Assets.strings.get("DIY LEAGUE");
        league.category = Activity.Category.DIY_COMPETITION;

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
        substitutesButton = w;

        w = new BenchSizeLabel();
        widgets.add(w);

        w = new BenchSizeButton();
        widgets.add(w);

        w = new OkButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    class TitleButton extends Button {

        public TitleButton() {
            setGeometry((game.settings.GUI_WIDTH - 520) / 2, 30, 520, 40);
            setColors(game.stateColor);
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
            league.longName = text;
        }
    }

    class SeasonPitchTypeButton extends Button {

        public SeasonPitchTypeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 175, 290, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 50, 175, 180, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 + 130, 175, 40, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 + 170, 175, 180, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 50, 175, 400, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 230, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class TimeButton extends Button {

        public TimeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 230, 240, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 285, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("NUMBER OF TEAMS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class NumberOfTeamsButton extends Button {

        public NumberOfTeamsButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 285, 240, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 340, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("PLAY EACH TEAM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PlayEachTeamButton extends Button {

        public PlayEachTeamButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 340, 240, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 395, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("POINTS FOR A WIN"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class PointsForAWinButton extends Button {

        public PointsForAWinButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 395, 240, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 450, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class SubstitutesButton extends Button {

        public SubstitutesButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 450, 240, 36);
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
            setGeometry(game.settings.GUI_WIDTH / 2 - 350, 505, 440, 36);
            setColors(0x800000, 0xB40000, 0x400000);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class BenchSizeButton extends Button {

        public BenchSizeButton() {
            setGeometry(game.settings.GUI_WIDTH / 2 + 110, 505, 240, 36);
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
            league.benchSize = Emath.slide(league.benchSize, 2, 5, n);
            league.substitutions = Math.min(league.substitutions, league.benchSize);
            setChanged(true);
            substitutesButton.setChanged(true);
        }

        @Override
        public void onUpdate() {
            setText(league.benchSize);
        }
    }

    class OkButton extends Button {
        public OkButton() {
            setColors(0x138B21, 0x1BC12F, 0x004814);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 590, 180, 36);
            setText(Assets.strings.get("OK"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectFolder(game, Assets.teamsFolder, league));
        }
    }

    class ExitButton extends Button {
        public ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.settings.GUI_WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }
}
