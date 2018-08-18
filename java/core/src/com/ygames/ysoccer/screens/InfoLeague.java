package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Pitch;

class InfoLeague extends GLScreen {

    private League league;

    InfoLeague(GLGame game) {
        super(game);

        background = game.stateBackground;

        league = (League) game.competition;

        Widget w;

        w = new TitleBar(league.name, game.stateColor.body);
        widgets.add(w);

        w = new WeatherButton();
        widgets.add(w);

        w = new SeasonStartButton();
        widgets.add(w);

        w = new SeasonSeparatorButton();
        widgets.add(w);

        w = new SeasonEndButton();
        widgets.add(w);

        w = new PitchTypeButton();
        widgets.add(w);

        w = new TimeLabel();
        widgets.add(w);

        w = new TimeButton();
        widgets.add(w);

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

        w = new BenchSizeButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        setSelectedWidget(w);
    }

    private class WeatherButton extends Button {

        WeatherButton() {
            setGeometry(game.gui.WIDTH / 2 - 350, 175, 290, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(league.getWeatherLabel()), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SeasonStartButton extends Button {

        SeasonStartButton() {
            setGeometry(game.gui.WIDTH / 2 - 50, 175, 180, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Month.getLabel(league.seasonStart)), Font.Align.CENTER, Assets.font14);
            setVisible(league.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class SeasonSeparatorButton extends Button {

        SeasonSeparatorButton() {
            setGeometry(game.gui.WIDTH / 2 + 132, 175, 36, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText("-", Font.Align.CENTER, Assets.font14);
            setActive(false);
            setVisible(league.weather == Competition.Weather.BY_SEASON);
        }
    }

    private class SeasonEndButton extends Button {

        SeasonEndButton() {
            setGeometry(game.gui.WIDTH / 2 + 170, 175, 180, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Month.getLabel(league.seasonEnd)), Font.Align.CENTER, Assets.font14);
            setVisible(league.weather == Competition.Weather.BY_SEASON);
            setActive(false);
        }
    }

    private class PitchTypeButton extends Button {

        PitchTypeButton() {
            setGeometry(game.gui.WIDTH / 2 - 50, 175, 400, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(Pitch.names[league.pitchType.ordinal()]), Font.Align.CENTER, Assets.font14);
            setVisible(league.weather == Competition.Weather.BY_PITCH_TYPE);
            setActive(false);
        }
    }

    private class TimeLabel extends Button {

        private TimeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 230, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TIME"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class TimeButton extends Button {

        TimeButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 230, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get(MatchSettings.getTimeLabel(league.time)), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class NumberOfTeamsLabel extends Button {

        NumberOfTeamsLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 285, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("NUMBER OF TEAMS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class NumberOfTeamsButton extends Button {

        NumberOfTeamsButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 285, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.numberOfTeams, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PlayEachTeamLabel extends Button {

        PlayEachTeamLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 340, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("PLAY EACH TEAM"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PlayEachTeamButton extends Button {

        PlayEachTeamButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 340, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(((char) 215) + "" + league.rounds, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PointsForAWinLabel extends Button {

        PointsForAWinLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 395, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("POINTS FOR A WIN"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class PointsForAWinButton extends Button {

        PointsForAWinButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 395, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.pointsForAWin, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesLabel extends Button {

        SubstitutesLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 450, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("SUBSTITUTES"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class SubstitutesButton extends Button {

        SubstitutesButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 450, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.substitutions, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeLabel extends Button {

        BenchSizeLabel() {
            setGeometry(game.gui.WIDTH / 2 - 350, 505, 440, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("BENCH SIZE"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class BenchSizeButton extends Button {

        BenchSizeButton() {
            setGeometry(game.gui.WIDTH / 2 + 110, 505, 240, 38);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(league.benchSize, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200, 0xFF6519, 0x803300);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new ViewStatistics(game));
        }
    }
}
